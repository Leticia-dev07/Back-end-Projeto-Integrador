package com.senac.pi.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.senac.pi.DTO.CategoriaDTO;
import com.senac.pi.entities.Categoria;
import com.senac.pi.entities.Curso;
import com.senac.pi.repositories.CategoriaRepository;
import com.senac.pi.repositories.CursoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository repository;

    @Autowired
    private CursoRepository cursoRepository; // Injeção necessária para buscar o curso

    @Transactional(readOnly = true)
    public List<CategoriaDTO> findAll() {
        return repository.findAll().stream()
                .map(CategoriaDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoriaDTO findById(Long id) {
        Categoria entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada: " + id));
        return new CategoriaDTO(entity);
    }

    /**
     * NOVO MÉTODO: Insere categoria vinculando pelo ID do curso na URL
     */
    @Transactional
    public CategoriaDTO insertComCurso(Long cursoId, Categoria obj) {
        // 1. Buscamos o curso no banco de dados
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado com ID: " + cursoId));

        // 2. Vinculamos o curso à categoria
        obj.setCurso(curso);

        // 3. Validamos duplicidade (mesma área no mesmo curso)
        if (repository.existsByAreaAndCurso(obj.getArea(), curso)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O curso ja tem essa categoria cadastrada");
        }

        obj = repository.save(obj);
        return new CategoriaDTO(obj);
    }

    /**
     * Método insert padrão (mantido por compatibilidade, caso use em outro lugar)
     */
    @Transactional
    public CategoriaDTO insert(Categoria obj) {
        if (obj.getCurso() == null || obj.getCurso().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID do curso é obrigatório.");
        }

        if (repository.existsByAreaAndCurso(obj.getArea(), obj.getCurso())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O curso ja tem essa categoria cadastrada");
        }

        obj = repository.save(obj);
        return new CategoriaDTO(obj);
    }

    @Transactional
    public CategoriaDTO update(Long id, Categoria obj) {
        try {
            Categoria entity = repository.getReferenceById(id);

            if (!entity.getArea().equalsIgnoreCase(obj.getArea()) && 
                repository.existsByAreaAndCurso(obj.getArea(), entity.getCurso())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Área já cadastrada neste curso.");
            }

            updateData(entity, obj);
            entity = repository.save(entity);
            return new CategoriaDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Id não encontrado: " + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Id não encontrado: " + id);
        }
        repository.deleteById(id);
    }

    private void updateData(Categoria entity, Categoria obj) {
        entity.setArea(obj.getArea());
        entity.setExigeComprovante(obj.getExigeComprovante());
        entity.setHorasPorCertificado(obj.getHorasPorCertificado());
        entity.setLimiteSubmissoesSemestre(obj.getLimiteSubmissoesSemestre());
    }
}