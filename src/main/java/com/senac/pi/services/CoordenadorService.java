package com.senac.pi.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.senac.pi.DTO.CoordenadorDTO;
import com.senac.pi.entities.Coordenador;
import com.senac.pi.entities.Curso;
import com.senac.pi.entities.enums.UserRole;
import com.senac.pi.repositories.CoordenadorRepository;
import com.senac.pi.repositories.CursoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CoordenadorService {

    // Logger manual para evitar conflitos com a IDE
    private static final Logger log = LoggerFactory.getLogger(CoordenadorService.class);

    @Autowired
    private CoordenadorRepository repository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<CoordenadorDTO> findAll() {
        log.info("### COORD ### Listando todos os coordenadores...");
        List<Coordenador> list = repository.findAll();
        log.info("### COORD ### {} coordenadores encontrados.", list.size());
        return list.stream()
                .map(CoordenadorDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CoordenadorDTO findById(Long id) {
        log.info("### COORD ### Buscando coordenador ID: {}", id);
        Coordenador entity = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("### COORD ### Falha: Coordenador ID {} não existe.", id);
                    return new EntityNotFoundException("Coordenador não encontrado: " + id);
                });
        return new CoordenadorDTO(entity);
    }

    @Transactional
    public CoordenadorDTO insert(CoordenadorDTO dto) {
        log.info("### COORD ### Iniciando cadastro do coordenador: {}", dto.name());
        try {
            Coordenador entity = new Coordenador();
            entity.setName(dto.name());
            entity.setEmail(dto.email());
            entity.setRole(UserRole.COORDENADOR);

            if (dto.password() != null && !dto.password().isBlank()) {
                log.info("### COORD ### Criptografando senha para o novo registro...");
                entity.setSenhaHash(passwordEncoder.encode(dto.password()));
            }

            entity = repository.save(entity);
            log.info("### COORD ### Coordenador cadastrado com sucesso! ID: {}", entity.getId());
            return new CoordenadorDTO(entity);
        } catch (Exception e) {
            log.error("### COORD ### Erro fatal ao inserir coordenador: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void vincularCurso(Long coordId, Long cursoId) {
        log.info("### VÍNCULO ### Tentando vincular Coordenador ID {} ao Curso ID {}", coordId, cursoId);
        
        Coordenador coord = repository.findById(coordId)
                .orElseThrow(() -> new EntityNotFoundException("Coordenador não encontrado"));

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado"));

        curso.getCoordenadores().add(coord);
        coord.getCursos().add(curso);

        cursoRepository.save(curso);
        log.info("### VÍNCULO ### Sucesso: Coordenador {} agora é responsável pelo curso {}.", coord.getName(), cursoId);
    }

    @Transactional
    public void desvincularCurso(Long coordId, Long cursoId) {
        log.info("### VÍNCULO ### Removendo vínculo do Coordenador ID {} com Curso ID {}", coordId, cursoId);
        
        Coordenador coord = repository.findById(coordId)
                .orElseThrow(() -> new EntityNotFoundException("Coordenador não encontrado"));

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado"));

        curso.getCoordenadores().remove(coord);
        coord.getCursos().remove(curso);

        cursoRepository.save(curso);
        log.info("### VÍNCULO ### Vínculo removido com sucesso.");
    }

    @Transactional
    public CoordenadorDTO update(Long id, CoordenadorDTO dto) {
        log.info("### COORD ### Atualizando dados do coordenador ID: {}", id);
        try {
            Coordenador entity = repository.getReferenceById(id);
            entity.setName(dto.name());
            entity.setEmail(dto.email());

            if (dto.password() != null && !dto.password().isBlank()) {
                log.info("### COORD ### Atualizando e recriptografando senha do ID: {}", id);
                entity.setSenhaHash(passwordEncoder.encode(dto.password()));
            }

            entity = repository.save(entity);
            log.info("### COORD ### Coordenador {} atualizado com sucesso.", entity.getName());
            return new CoordenadorDTO(entity);
        } catch (EntityNotFoundException e) {
            log.error("### COORD ### Erro no update: ID {} não localizado.", id);
            throw new EntityNotFoundException("Id não encontrado: " + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        log.info("### COORD ### Solicitada exclusão do Coordenador ID: {}", id);
        if (!repository.existsById(id)) {
            log.warn("### COORD ### Tentativa de exclusão falhou: ID {} inexistente.", id);
            throw new EntityNotFoundException("Id não encontrado: " + id);
        }
        repository.deleteById(id);
        log.info("### COORD ### Coordenador ID {} removido do sistema.", id);
    }
}