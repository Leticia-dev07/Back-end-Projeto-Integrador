package com.senac.pi.config;

import java.time.Instant;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.senac.pi.entities.Aluno;
import com.senac.pi.entities.Categoria;
import com.senac.pi.entities.Certificado;
import com.senac.pi.entities.Coordenador;
import com.senac.pi.entities.Curso;
import com.senac.pi.entities.Submissao;
import com.senac.pi.entities.SuperAdmin;
import com.senac.pi.entities.enums.StatusSubmissao;
import com.senac.pi.repositories.AlunoRepository;
import com.senac.pi.repositories.CategoriaRepository;
import com.senac.pi.repositories.CoordenadorRepository;
import com.senac.pi.repositories.CursoRepository;
import com.senac.pi.repositories.SubmissaoRepository;
import com.senac.pi.repositories.SuperAdminRepository;
import com.senac.pi.repositories.UserRepository;

@Configuration
public class TestConfig implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SuperAdminRepository superAdminRepository;

    @Autowired
    private CoordenadorRepository coordenadorRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private SubmissaoRepository submissaoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.count() == 0) {

            System.out.println("SISTEMA: Banco vazio. Populando...");

            String senha = passwordEncoder.encode("123456");

            // ========================
            // SUPER ADMIN
            // ========================
            SuperAdmin admin = new SuperAdmin(
                null,
                "Administrador Geral",
                "admin@senac.com",
                senha
            );
            superAdminRepository.save(admin);

            // ========================
            // COORDENADOR
            // ========================
            Coordenador coord = new Coordenador(
                null,
                "Letícia",
                "coordenador@senac.com",
                senha
            );
            coord = coordenadorRepository.save(coord);

            // ========================
            // CURSO
            // ========================
            Curso curso = new Curso(
                null,
                "Análise e Desenvolvimento de Sistemas",
                "Curso superior focado em desenvolvimento",
                200
            );

            curso.getCoordenadores().add(coord);
            curso = cursoRepository.save(curso);

            coord.getCursos().add(curso);
            coordenadorRepository.save(coord);

            // ========================
            // CATEGORIAS
            // ========================
            Categoria cat1 = new Categoria();
            cat1.setArea("Cursos Complementares");
            cat1.setHorasPorCertificado(15);
            cat1.setLimiteSubmissoesSemestre(5);
            cat1.setCurso(curso);

            Categoria cat2 = new Categoria();
            cat2.setArea("Eventos Acadêmicos e Hackathons");
            cat2.setHorasPorCertificado(20);
            cat2.setLimiteSubmissoesSemestre(3);
            cat2.setCurso(curso);

            categoriaRepository.saveAll(Arrays.asList(cat1, cat2));

            // ========================
            // ALUNO
            // ========================
            Aluno aluno = new Aluno(
                null,
                "Caio Victor de Moura Paschoal",
                "aluno@senac.com",
                senha,
                "202501001",
                "ADS-Noite",
                0
            );

            aluno.addCurso(curso);
            aluno = alunoRepository.save(aluno);

            // ========================
            // SUBMISSÃO 1
            // ========================
            Submissao sub1 = new Submissao(
                null,
                Instant.parse("2026-05-04T10:00:00Z"),
                StatusSubmissao.PENDENTE,
                15,
                null,
                aluno,
                cat1
            );

            Certificado cert1 = new Certificado();
            cert1.setNomeArquivo("certificado1.pdf");
            cert1.setTipoArquivo("application/pdf");

            // 🔥 URL DIRETA PARA A PASTA uploads
            cert1.setUrlArquivo("http://localhost:8080/certificados/certificado1.pdf");

            sub1.setCertificado(cert1);
            cert1.setSubmissao(sub1);

            // ========================
            // SUBMISSÃO 2
            // ========================
            Submissao sub2 = new Submissao(
                null,
                Instant.parse("2026-05-05T14:30:00Z"),
                StatusSubmissao.PENDENTE,
                20,
                null,
                aluno,
                cat2
            );

            Certificado cert2 = new Certificado();
            cert2.setNomeArquivo("certificado2.pdf");
            cert2.setTipoArquivo("application/pdf");

            cert2.setUrlArquivo("http://localhost:8080/certificados/certificado2.pdf");

            sub2.setCertificado(cert2);
            cert2.setSubmissao(sub2);

            // ========================
            // SALVAR
            // ========================
            submissaoRepository.saveAll(Arrays.asList(sub1, sub2));

            System.out.println("====================================");
            System.out.println("BANCO POPULADO COM SUCESSO");
            System.out.println("admin@senac.com | 123456");
            System.out.println("coordenador@senac.com | 123456");
            System.out.println("aluno@senac.com | 123456");
            System.out.println("====================================");
        }
    }
}