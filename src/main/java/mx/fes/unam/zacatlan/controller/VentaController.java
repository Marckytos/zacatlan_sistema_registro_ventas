package mx.fes.unam.zacatlan.controller;

import jakarta.validation.Valid;
import mx.fes.unam.zacatlan.model.entities.producto.ProductoDTO;
import mx.fes.unam.zacatlan.model.entities.venta.VentaDTO;
import mx.fes.unam.zacatlan.model.entities.vendedor.VendedorDTO;
import mx.fes.unam.zacatlan.service.producto.ProductoService;
import mx.fes.unam.zacatlan.service.venta.PdfVentaService;
import mx.fes.unam.zacatlan.service.venta.VentaService;
import mx.fes.unam.zacatlan.service.vendedor.VendedorService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ContentDisposition;
import java.nio.charset.StandardCharsets;

import java.util.List;

@Controller
@RequestMapping("venta")
public class VentaController {

    private final VentaService ventaService;
    private final VendedorService vendedorService;
    private final ProductoService productoService;
    private final PdfVentaService pdfVentaService;

    public VentaController(VentaService ventaService, VendedorService vendedorService,
                           ProductoService productoService, PdfVentaService pdfVentaService) {
        this.ventaService = ventaService;
        this.vendedorService = vendedorService;
        this.productoService = productoService;
        this.pdfVentaService = pdfVentaService;
    }

    // ==================== para el VENDEDOR ====================

    @GetMapping("mis-ventas")
    public String misVentas(Authentication auth, Model model) {
        VendedorDTO vendedor = vendedorService.obtenerPorUsername(auth.getName());
        if (vendedor == null) {
            return "redirect:/login";
        }
        model.addAttribute("lista", ventaService.listarVentasPorVendedor(vendedor.getId()));
        model.addAttribute("vendedor", vendedor);
        model.addAttribute("contenido", "Mis Ventas");
        return "venta/mis-ventas";
    }

    @GetMapping("registrar")
    public String registrar(Model model, Authentication auth) {
        VendedorDTO vendedor = vendedorService.obtenerPorUsername(auth.getName());
        if (vendedor == null) {
            return "redirect:/login";
        }
        if (!Boolean.TRUE.equals(vendedor.getActivo())) {
            return "redirect:/?inactivo";
        }
        VentaDTO venta = new VentaDTO();
        venta.setVendedor(vendedor);
        venta.setProducto(new ProductoDTO());
        model.addAttribute("venta", venta);
        model.addAttribute("productos", productoService.listarProductosAprobados());
        model.addAttribute("contenido", "Registrar Venta");
        return "venta/alta-venta";
    }

    @PostMapping("salvar")
    public String salvar(@Valid @ModelAttribute("venta") VentaDTO venta,
                         BindingResult br,
                         Authentication auth,
                         RedirectAttributes ra,
                         Model model) {

        // ========= BLINDAJE CLAVE =========
        // Ignoramos el vendedor que venga del formulario.
        // Usamos el vendedor autenticado en el servidor.

        VendedorDTO vendedorReal = vendedorService.obtenerPorUsername(auth.getName());
        if (vendedorReal == null) {
            return "redirect:/login";
        }
        venta.setVendedor(vendedorReal);
        // ===================================

        if (br.hasErrors()) {
            model.addAttribute("productos", productoService.listarProductosAprobados());
            model.addAttribute("contenido", "Registrar Venta");
            return "venta/alta-venta";
        }
        try {
            ventaService.guardarVenta(venta);
            ra.addFlashAttribute("success", "Venta registrada exitosamente");
            return "redirect:/venta/mis-ventas";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("productos", productoService.listarProductosAprobados());
            model.addAttribute("contenido", "Registrar Venta");
            return "venta/alta-venta";
        }
    }

    @GetMapping("mis-ventas/pdf")
    public ResponseEntity<byte[]> pdfPropio(Authentication auth) {
        // 1. Obtener vendedor del usuario autenticado (NUNCA del formulario o URL)
        VendedorDTO vendedor = vendedorService.obtenerPorUsername(auth.getName());
        if (vendedor == null) {
            return ResponseEntity.notFound().build();
        }

        // 2. Obtener SOLO las ventas de este vendedor
        List<VentaDTO> ventas = ventaService.listarVentasPorVendedor(vendedor.getId());

        // 3. Para mas proteccion: Limitar el tamaño del PDF para evitar DoS
        if (ventas.size() > 5000) {
            return ResponseEntity.badRequest()
                    .header("X-Error", "Demasiadas ventas para un solo PDF. Contacte al administrador.")
                    .build();
        }

        // 4. Generar el PDF
        byte[] pdf = pdfVentaService.generatePdf(ventas, "Reporte de Ventas - " + vendedor.getNombre());

        // 5. Headers de respuesta
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        // Proteccion: ContentDisposition con UTF-8 (maneja acentos en nombres de archivo)
        org.springframework.http.ContentDisposition disposition =
                org.springframework.http.ContentDisposition.attachment()
                        .filename("mis-ventas-" + vendedor.getUsername() + ".pdf",
                                java.nio.charset.StandardCharsets.UTF_8)
                        .build();
        headers.setContentDisposition(disposition);

        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    // ==================== para el ADMIN ====================

    @GetMapping("ver_lista")
    public String listarAdmin(Model model) {
        model.addAttribute("lista", ventaService.listarVentas());
        model.addAttribute("contenido", "Lista de Ventas");
        return "venta/venta";
    }

    @GetMapping("editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model) {
        VentaDTO dto = ventaService.obtenerVenta(id);
        if (dto == null) return "redirect:/venta/ver_lista";
        model.addAttribute("venta", dto);
        model.addAttribute("contenido", "Modificar Venta");
        return "venta/editar-venta";
    }

    @PostMapping("actualizar")
    public String actualizar(@ModelAttribute("venta") VentaDTO venta,
                             RedirectAttributes ra) {
        try {
            ventaService.modificarVenta(venta);
            ra.addFlashAttribute("success", "Venta modificada");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/venta/ver_lista";
    }

    @GetMapping("pdf")
    public ResponseEntity<byte[]> pdfAdmin() {
        List<VentaDTO> ventas = ventaService.listarVentas();
        byte[] pdf = pdfVentaService.generatePdf(ventas, "Reporte General de Ventas");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte-ventas.pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }


}