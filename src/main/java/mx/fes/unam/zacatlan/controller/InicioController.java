package mx.fes.unam.zacatlan.controller;

import mx.fes.unam.zacatlan.model.entities.venta.VentaDTO;
import mx.fes.unam.zacatlan.model.entities.vendedor.VendedorDTO;
import mx.fes.unam.zacatlan.service.venta.VentaService;
import mx.fes.unam.zacatlan.service.vendedor.VendedorService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class InicioController {

    private final VendedorService vendedorService;
    private final VentaService ventaService;

    public InicioController(VendedorService vendedorService, VentaService ventaService) {
        this.vendedorService = vendedorService;
        this.ventaService = ventaService;
    }

    @GetMapping("/")
    public String inicio(Model model, Authentication auth) {
        if (auth == null) {
            return "redirect:/login";
        }

        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("username", auth.getName());
        model.addAttribute("esAdmin", esAdmin);
        model.addAttribute("inactivo", false);

        if (esAdmin) {
            // Admin: estadísticas generales
            List<VentaDTO> todas = ventaService.listarVentas();
            BigDecimal totalHoy = todas.stream()
                    .filter(v -> v.getFecha() != null && v.getFecha().toLocalDate().equals(LocalDate.now()))
                    .map(VentaDTO::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            model.addAttribute("totalVentas", todas.size());
            model.addAttribute("totalHoy", totalHoy);
            model.addAttribute("contenido", "Panel de Administración");
        } else {
            // Vendedor: sus ventas del día
            VendedorDTO vendedor = vendedorService.obtenerPorUsername(auth.getName());
            if (vendedor == null) {
                return "redirect:/login?error";
            }

            if (!Boolean.TRUE.equals(vendedor.getActivo())) {
                model.addAttribute("vendedor", vendedor);
                model.addAttribute("inactivo", true);
                model.addAttribute("contenido", "Cuenta Inactiva");
                return "inicio";
            }

            List<VentaDTO> misVentas = ventaService.listarVentasPorVendedor(vendedor.getId());
            List<VentaDTO> ventasHoy = misVentas.stream()
                    .filter(v -> v.getFecha() != null && v.getFecha().toLocalDate().equals(LocalDate.now()))
                    .toList();

            BigDecimal totalHoy = ventasHoy.stream()
                    .map(VentaDTO::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            model.addAttribute("vendedor", vendedor);
            model.addAttribute("ventasHoy", ventasHoy);
            model.addAttribute("totalHoy", totalHoy);
            model.addAttribute("contenido", "Bienvenido, " + vendedor.getNombre());
        }

        return "inicio";
    }
}