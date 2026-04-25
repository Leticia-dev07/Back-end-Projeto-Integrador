package com.senac.pi.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.senac.pi.DTO.CursoDTO;
import com.senac.pi.entities.Curso;
import com.senac.pi.repositories.CursoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CursoService {

    @Autowired
    private CursoRepository repository;

    @Transactional(readOnly = true)
    public List<CursoDTO> findAll() {
        List<Curso> list = repository.findAll();
        return list.stream().map(CursoDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CursoDTO findById(Long id) {
        Curso entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado: " + id));
        return new CursoDTO(entity);
    }

    @Transactional
    public CursoDTO insert(Curso obj) {
        // REGRA DE NEGÓCIO: Verificar se o nome já existe
        if (repository.existsByNome(obj.getNome())) {
            throw new RuntimeException("Já existe um curso cadastrado com o nome: " + obj.getNome());
        }
        
        obj = repository.save(obj);
        return new CursoDTO(obj);
    }

    @Transactional
    public CursoDTO update(Long id, Curso obj) {
        try {
            Curso entity = repository.getReferenceById(id);
            
            // Validação extra: Se o nome mudou, verifica se o novo nome já está em uso por outro curso
            if (!entity.getNome().equals(obj.getNome()) && repository.existsByNome(obj.getNome())) {
                throw new RuntimeException("Não é possível atualizar: o nome '" + obj.getNome() + "' já está em uso.");
            }

            updateData(entity, obj);
            entity = repository.save(entity);
            return new CursoDTO(entity);
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

    private void updateData(Curso entity, Curso obj) {
        entity.setNome(obj.getNome());
        entity.setDescricao(obj.getDescricao());
        entity.setCargaHorariaMax(obj.getCargaHorariaMax());
    }
}