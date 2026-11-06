package mx.fes.unam.zacatlan.controller;

import jakarta.validation.Valid;
import mx.fes.unam.zacatlan.model.entities.bloque.BloqueDTO;
import mx.fes.unam.zacatlan.model.entities.producto.ProductoDTO;
import mx.fes.unam.zacatlan.service.bloque.BloqueService;
import mx.fes.unam.zacatlan.service.producto.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("producto")
public class ProductoController {

    private final ProductoService productoService;
    private final BloqueService bloqueService;

    public ProductoController(ProductoService productoService, BloqueService bloqueService) {
        this.productoService = productoService;
        this.bloqueService = bloqueService;
    }

    @GetMapping("ver_lista")
    public String listar(Model model) {
        model.addAttribute("lista", productoService.listarProductos());
        model.addAttribute("contenido", "Lista de Productos");
        return "producto/producto";
    }

    @GetMapping("alta-producto")
    public String alta(Model model) {
        model.addAttribute("producto", new ProductoDTO());
        model.addAttribute("bloques", bloqueService.listarBloques());
        model.addAttribute("contenido", "Alta de Producto");
        ProductoDTO producto = new ProductoDTO();
        producto.setBloque(new BloqueDTO());
        model.addAttribute("producto", producto);
        return "producto/alta-producto";
    }

    @PostMapping("salvar-producto")
    public String guardar(@Valid @ModelAttribute("producto") ProductoDTO producto,
                          BindingResult br,
                          RedirectAttributes ra,
                          Model model) {
        if (br.hasErrors()) {
            model.addAttribute("bloques", bloqueService.listarBloques());
            model.addAttribute("contenido", "Alta de Producto");
            return "producto/alta-producto";
        }
        // Por defecto, un producto nuevo no está aprobado
        if (producto.getId() == null) {
            producto.setAprobado(false);
        }
        productoService.guardarProducto(producto);
        ra.addFlashAttribute("success", "Producto guardado");
        return "redirect:/producto/ver_lista";
    }

    @GetMapping("editar-producto/{id}")
    public String editar(@PathVariable("id") Long id, Model model) {
        ProductoDTO dto = productoService.obtenerProducto(id);
        if (dto == null) return "redirect:/producto/ver_lista";
        model.addAttribute("producto", dto);
        model.addAttribute("bloques", bloqueService.listarBloques());
        model.addAttribute("contenido", "Editar Producto");
        return "producto/alta-producto";
    }

    @GetMapping("aprobar/{id}")
    public String aprobar(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            productoService.aprobarProducto(id);
            ra.addFlashAttribute("success", "Producto aprobado para venta");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/producto/ver_lista";
    }

    @GetMapping("eliminar-producto/{id}")
    public String eliminar(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            productoService.eliminarProducto(id);
            ra.addFlashAttribute("success", "Producto eliminado");
        } catch (Exception e) {
            ra.addFlashAttribute("error",
                    "No se puede eliminar: tiene ventas asociadas");
        }
        return "redirect:/producto/ver_lista";
    }
}