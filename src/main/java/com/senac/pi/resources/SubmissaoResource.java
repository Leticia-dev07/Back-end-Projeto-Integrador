package com.senac.pi.resources;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.senac.pi.DTO.SubmissaoDTO;
import com.senac.pi.entities.Submissao;
import com.senac.pi.services.AlunoService;
import com.senac.pi.services.SubmissaoService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/submissoes")
@CrossOrigin(origins = "*")
@Slf4j
public class SubmissaoResource {
	
	private static final Logger log = LoggerFactory.getLogger(SubmissaoResource.class);

    @Autowired
    private SubmissaoService service;

    @GetMapping
    public ResponseEntity<List<SubmissaoDTO>> findAll() {
        List<SubmissaoDTO> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<SubmissaoDTO> findById(@PathVariable Long id) {
        SubmissaoDTO dto = service.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    /**
     * Retorna os bytes do arquivo (PDF) armazenado no banco para a submissão informada.
     * O frontend faz um fetch autenticado (Bearer token) e exibe via Blob URL no iframe.
     */
    @GetMapping(value = "/{id}/arquivo")
    public ResponseEntity<byte[]> downloadArquivo(@PathVariable Long id) {
        Submissao submissao = service.findEntityById(id);

        byte[] dados = submissao.getDadosArquivo();

        if (dados == null || dados.length == 0) {
            log.warn("### ARQUIVO ### Submissão ID {} não possui arquivo.", id);
            return ResponseEntity.noContent().build();
        }

        String contentType = submissao.getTipoArquivo() != null
                ? submissao.getTipoArquivo()
                : "application/pdf";

        String nomeArquivo = submissao.getNomeArquivo() != null
                ? submissao.getNomeArquivo()
                : "certificado.pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomeArquivo + "\"")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(dados.length))
                .body(dados);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SubmissaoDTO> insert(
            @RequestPart("submissao") Submissao obj,
            @RequestPart("file") MultipartFile file) {

        SubmissaoDTO dto = service.insert(obj, file);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}/aprovar")
    public ResponseEntity<SubmissaoDTO> aprovar(@PathVariable Long id) {
        SubmissaoDTO dto = service.aprovar(id);
        return ResponseEntity.ok().body(dto);
    }

    @PutMapping(value = "/{id}/rejeitar")
    public ResponseEntity<SubmissaoDTO> rejeitar(@PathVariable Long id, @RequestBody String observacao) {
        SubmissaoDTO dto = service.rejeitar(id, observacao);
        return ResponseEntity.ok().body(dto);
    }
}