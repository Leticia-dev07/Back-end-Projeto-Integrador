package com.senac.pi.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.senac.pi.DTO.CategoriaDTO;
import com.senac.pi.entities.Categoria;
import com.senac.pi.repositories.CategoriaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository repository;

    @Transactional(readOnly = true)
    public List<CategoriaDTO> findAll() {
        return repository.findAll().stream().map(CategoriaDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoriaDTO findById(Long id) {
        Categoria entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada: " + id));
        return new CategoriaDTO(entity);
    }

    @Transactional
    public CategoriaDTO insert(Categoria obj) {
        // REGRA DE NEGÓCIO: Evitar duplicidade de área no mesmo curso
        if (repository.existsByAreaAndCurso(obj.getArea(), obj.getCurso())) {
            throw new RuntimeException("A categoria '" + obj.getArea() + "' já está cadastrada para este curso.");
        }
        obj = repository.save(obj);
        return new CategoriaDTO(obj);
    }

    @Transactional
    public CategoriaDTO update(Long id, Categoria obj) {
        try {
            Categoria entity = repository.getReferenceById(id);
            
            // Se o nome da área mudou, verificamos se a nova área já existe no curso
            if (!entity.getArea().equals(obj.getArea()) && 
                repository.existsByAreaAndCurso(obj.getArea(), entity.getCurso())) {
                throw new RuntimeException("Não é possível alterar: a área '" + obj.getArea() + "' já existe neste curso.");
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
        
        // NOVOS ATRIBUTOS ATUALIZADOS
        entity.setHorasPorCertificado(obj.getHorasPorCertificado());
        entity.setLimiteSubmissoesSemestre(obj.getLimiteSubmissoesSemestre());
        
        // Nota: limiteHoras foi removido conforme sua solicitação de simplificação
    }
}