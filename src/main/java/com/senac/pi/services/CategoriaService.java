package com.senac.pi.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    // Logger manual para evitar erros de compilação da IDE
    private static final Logger log = LoggerFactory.getLogger(CategoriaService.class);

    @Autowired
    private CategoriaRepository repository;

    @Autowired
    private CursoRepository cursoRepository;

    @Transactional(readOnly = true)
    public List<CategoriaDTO> findAll() {
        log.info("Listando todas as categorias cadastradas.");
        return repository.findAll().stream()
                .map(CategoriaDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoriaDTO findById(Long id) {
        log.info("Buscando categoria pelo ID: {}", id);
        Categoria entity = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Categoria ID {} não encontrada.", id);
                    return new EntityNotFoundException("Categoria não encontrada: " + id);
                });
        return new CategoriaDTO(entity);
    }

    @Transactional
    public CategoriaDTO insertComCurso(Long cursoId, Categoria obj) {
        log.info("Tentando inserir categoria '{}' para o curso ID: {}", obj.getArea(), cursoId);
        
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> {
                    log.error("Erro no vínculo: Curso ID {} inexistente.", cursoId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado com ID: " + cursoId);
                });

        obj.setCurso(curso);

        if (repository.existsByAreaAndCurso(obj.getArea(), curso)) {
            log.warn("Falha na inserção: A área '{}' já existe para o curso '{}'.", obj.getArea(), cursoId);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O curso ja tem essa categoria cadastrada");
        }

        obj = repository.save(obj);
        log.info("Categoria '{}' salva com sucesso para o curso ID: {}", obj.getArea(), cursoId);
        return new CategoriaDTO(obj);
    }

    @Transactional
    public CategoriaDTO insert(Categoria obj) {
        log.info("Iniciando inserção padrão de categoria: {}", obj.getArea());
        
        if (obj.getCurso() == null || obj.getCurso().getId() == null) {
            log.warn("Tentativa de inserção de categoria sem curso associado.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID do curso é obrigatório.");
        }

        if (repository.existsByAreaAndCurso(obj.getArea(), obj.getCurso())) {
            log.warn("Área '{}' duplicada para este curso.", obj.getArea());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O curso ja tem essa categoria cadastrada");
        }

        obj = repository.save(obj);
        log.info("Categoria salva com sucesso. ID gerado: {}", obj.getId());
        return new CategoriaDTO(obj);
    }

    @Transactional
    public CategoriaDTO update(Long id, Categoria obj) {
        log.info("Atualizando categoria ID: {}", id);
        try {
            Categoria entity = repository.getReferenceById(id);

            if (!entity.getArea().equalsIgnoreCase(obj.getArea()) && 
                repository.existsByAreaAndCurso(obj.getArea(), entity.getCurso())) {
                log.warn("Erro ao atualizar: A nova área '{}' já existe para o curso deste registro.", obj.getArea());
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Área já cadastrada neste curso.");
            }

            updateData(entity, obj);
            entity = repository.save(entity);
            log.info("Categoria ID {} atualizada com sucesso.", id);
            return new CategoriaDTO(entity);
        } catch (EntityNotFoundException e) {
            log.error("ID {} não encontrado para atualização.", id);
            throw new EntityNotFoundException("Id não encontrado: " + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        log.info("Solicitada exclusão da categoria ID: {}", id);
        if (!repository.existsById(id)) {
            log.error("Falha ao excluir: ID {} não existe.", id);
            throw new EntityNotFoundException("Id não encontrado: " + id);
        }
        repository.deleteById(id);
        log.info("Categoria ID {} removida com sucesso.", id);
    }

    private void updateData(Categoria entity, Categoria obj) {
        entity.setArea(obj.getArea());
        entity.setExigeComprovante(obj.getExigeComprovante());
        entity.setHorasPorCertificado(obj.getHorasPorCertificado());
        entity.setLimiteSubmissoesSemestre(obj.getLimiteSubmissoesSemestre());
    }
}