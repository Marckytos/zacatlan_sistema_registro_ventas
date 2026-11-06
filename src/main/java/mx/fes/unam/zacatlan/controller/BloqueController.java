package mx.fes.unam.zacatlan.controller;

import jakarta.validation.Valid;
import mx.fes.unam.zacatlan.model.entities.bloque.BloqueDTO;
import mx.fes.unam.zacatlan.service.bloque.BloqueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("bloque")
public class BloqueController {

    private final BloqueService bloqueService;

    public BloqueController(BloqueService bloqueService) {
        this.bloqueService = bloqueService;
    }

    @GetMapping("ver_lista")
    public String listar(Model model) {
        model.addAttribute("lista", bloqueService.listarBloques());
        model.addAttribute("contenido", "Lista de Bloques");
        return "bloque/bloque";
    }

    @GetMapping("alta-bloque")
    public String alta(Model model) {
        model.addAttribute("bloque", new BloqueDTO());
        model.addAttribute("contenido", "Alta de Bloque");
        return "bloque/alta-bloque";
    }

    @PostMapping("salvar-bloque")
    public String guardar(@Valid @ModelAttribute("bloque") BloqueDTO bloque,
                          BindingResult br,
                          RedirectAttributes ra) {
        if (br.hasErrors()) {
            return "bloque/alta-bloque";
        }
        bloqueService.guardarBloque(bloque);
        ra.addFlashAttribute("success", "Bloque guardado");
        return "redirect:/bloque/ver_lista";
    }

    @GetMapping("editar-bloque/{id}")
    public String editar(@PathVariable("id") Long id, Model model) {
        BloqueDTO dto = bloqueService.obtenerBloque(id);
        if (dto == null) return "redirect:/bloque/ver_lista";
        model.addAttribute("bloque", dto);
        model.addAttribute("contenido", "Editar Bloque");
        return "bloque/alta-bloque";
    }

    @GetMapping("eliminar-bloque/{id}")
    public String eliminar(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            bloqueService.eliminarBloque(id);
            ra.addFlashAttribute("success", "Bloque eliminado");
        } catch (Exception e) {
            ra.addFlashAttribute("error",
                    "No se puede eliminar: tiene productos asociados");
        }
        return "redirect:/bloque/ver_lista";
    }
}