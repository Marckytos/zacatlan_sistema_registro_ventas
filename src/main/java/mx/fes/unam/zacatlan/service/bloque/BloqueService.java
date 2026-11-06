package mx.fes.unam.zacatlan.service.bloque;

import mx.fes.unam.zacatlan.model.entities.bloque.BloqueDTO;
import mx.fes.unam.zacatlan.model.entities.bloque.BloqueEntity;
import mx.fes.unam.zacatlan.repository.BloqueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BloqueService {

    private final BloqueMapper bloqueMapper;
    private final BloqueRepository bloqueRepository;

    public BloqueService(BloqueMapper bloqueMapper, BloqueRepository bloqueRepository) {
        this.bloqueMapper = bloqueMapper;
        this.bloqueRepository = bloqueRepository;
    }

    public BloqueDTO guardarBloque(BloqueDTO bloqueDTO) {
        BloqueEntity entity = bloqueRepository.save(bloqueMapper.toEntity(bloqueDTO));
        return bloqueMapper.toDTO(entity);
    }

    public BloqueDTO obtenerBloque(Long id) {
        return bloqueMapper.toDTO(bloqueRepository.findById(id).orElse(null));
    }

    public void eliminarBloque(Long id) {
        bloqueRepository.deleteById(id);
    }

    public List<BloqueDTO> listarBloques() {
        return bloqueRepository.findAll().stream().map(bloqueMapper::toDTO).toList();
    }
}