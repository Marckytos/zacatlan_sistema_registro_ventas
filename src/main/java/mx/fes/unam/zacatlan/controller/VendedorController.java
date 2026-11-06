package mx.fes.unam.zacatlan.controller;

import jakarta.validation.Valid;
import mx.fes.unam.zacatlan.model.entities.vendedor.VendedorDTO;
import mx.fes.unam.zacatlan.model.entities.vendedor.VendedorRegistroDTO;
import mx.fes.unam.zacatlan.service.vendedor.VendedorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("vendedor")
public class VendedorController {

    private final VendedorService vendedorService;

    public VendedorController(VendedorService vendedorService) {
        this.vendedorService = vendedorService;
    }

    @GetMapping("ver_lista")
    public String listar(Model model) {
        model.addAttribute("lista", vendedorService.listarVendedores());
        model.addAttribute("contenido", "Lista de Vendedores");
        return "vendedor/vendedor";
    }

    @GetMapping("alta-vendedor")
    public String alta(Model model) {
        model.addAttribute("vendedor", new VendedorRegistroDTO());
        model.addAttribute("contenido", "Alta de Vendedor");
        return "vendedor/alta-vendedor";
    }

    @PostMapping("salvar-vendedor")
    public String guardar(@Valid @ModelAttribute("vendedor") VendedorRegistroDTO registro,
                          BindingResult br,
                          RedirectAttributes ra,
                          Model model) {
        if (br.hasErrors()) {
            model.addAttribute("contenido", "Alta de Vendedor");
            return "vendedor/alta-vendedor";
        }
        try {
            vendedorService.registrarVendedor(registro);
            ra.addFlashAttribute("success", "Vendedor registrado con éxito");
            return "redirect:/vendedor/ver_lista";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("contenido", "Alta de Vendedor");
            return "vendedor/alta-vendedor";
        }
    }

    @GetMapping("editar-vendedor/{id}")
    public String editar(@PathVariable("id") Long id, Model model) {
        VendedorRegistroDTO registro = vendedorService.obtenerVendedorParaEditar(id);
        model.addAttribute("vendedor", registro);
        model.addAttribute("contenido", "Editar Vendedor");
        return "vendedor/alta-vendedor";
    }

    @PostMapping("actualizar-vendedor")
    public String actualizar(@Valid @ModelAttribute("vendedor") VendedorRegistroDTO registro,
                             BindingResult br,
                             RedirectAttributes ra,
                             Model model) {
        if (br.hasErrors()) {
            model.addAttribute("contenido", "Editar Vendedor");
            return "vendedor/alta-vendedor";
        }
        try {
            vendedorService.actualizarVendedor(registro.getId(), registro);
            ra.addFlashAttribute("success", "Vendedor actualizado");
            return "redirect:/vendedor/ver_lista";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("contenido", "Editar Vendedor");
            return "vendedor/alta-vendedor";
        }
    }

    @GetMapping("desactivar/{id}")
    public String desactivar(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            vendedorService.desactivarVendedor(id);
            ra.addFlashAttribute("success",
                    "Vendedor desactivado. Se guardó la fecha de su última venta.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/vendedor/ver_lista";
    }

    @GetMapping("activar/{id}")
    public String activar(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            vendedorService.activarVendedor(id);
            ra.addFlashAttribute("success", "Vendedor activado");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/vendedor/ver_lista";
    }
}