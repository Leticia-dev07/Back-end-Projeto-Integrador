package com.senac.pi.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.senac.pi.DTO.CursoDTO;
import com.senac.pi.entities.Curso;
import com.senac.pi.repositories.CursoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CursoService {

    // Logger manual para visibilidade no console
    private static final Logger log = LoggerFactory.getLogger(CursoService.class);

    @Autowired
    private CursoRepository repository;

    @Transactional(readOnly = true)
    public List<CursoDTO> findAll() {
        log.info("### CURSO ### Buscando todos os cursos cadastrados.");
        List<Curso> list = repository.findAll();
        log.info("### CURSO ### Total de cursos retornados: {}", list.size());
        return list.stream().map(CursoDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CursoDTO findById(Long id) {
        log.info("### CURSO ### Consultando curso ID: {}", id);
        Curso entity = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("### CURSO ### Erro: Curso com ID {} não foi localizado.", id);
                    return new EntityNotFoundException("Curso não encontrado: " + id);
                });
        return new CursoDTO(entity);
    }

    @Transactional
    public CursoDTO insert(Curso obj) {
        log.info("### CURSO ### Tentando cadastrar novo curso: {}", obj.getNome());
        
        if (repository.existsByNome(obj.getNome())) {
            log.warn("### CURSO ### Falha ao inserir: O nome '{}' já está em uso.", obj.getNome());
            throw new RuntimeException("Já existe um curso cadastrado com o nome: " + obj.getNome());
        }
        
        obj = repository.save(obj);
        log.info("### CURSO ### Curso '{}' cadastrado com sucesso. ID: {}", obj.getNome(), obj.getId());
        return new CursoDTO(obj);
    }

    @Transactional
    public CursoDTO update(Long id, Curso obj) {
        log.info("### CURSO ### Iniciando atualização do curso ID: {}", id);
        try {
            Curso entity = repository.getReferenceById(id);
            
            if (!entity.getNome().equals(obj.getNome()) && repository.existsByNome(obj.getNome())) {
                log.warn("### CURSO ### Conflito: Tentativa de renomear curso para '{}', mas esse nome já existe.", obj.getNome());
                throw new RuntimeException("Não é possível atualizar: o nome '" + obj.getNome() + "' já está em uso.");
            }

            updateData(entity, obj);
            entity = repository.save(entity);
            log.info("### CURSO ### Curso ID {} atualizado com sucesso.", id);
            return new CursoDTO(entity);
        } catch (EntityNotFoundException e) {
            log.error("### CURSO ### Falha no update: ID {} não encontrado.", id);
            throw new EntityNotFoundException("Id não encontrado: " + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        log.info("### CURSO ### Solicitada remoção do curso ID: {}", id);
        if (!repository.existsById(id)) {
            log.error("### CURSO ### Falha na deleção: ID {} inexistente.", id);
            throw new EntityNotFoundException("Id não encontrado: " + id);
        }
        repository.deleteById(id);
        log.info("### CURSO ### Curso ID {} removido com sucesso.", id);
    }

    private void updateData(Curso entity, Curso obj) {
        entity.setNome(obj.getNome());
        entity.setDescricao(obj.getDescricao());
        entity.setCargaHorariaMax(obj.getCargaHorariaMax());
    }
}