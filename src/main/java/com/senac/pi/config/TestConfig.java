package com.senac.pi.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.senac.pi.entities.Aluno;
import com.senac.pi.entities.Categoria;
import com.senac.pi.entities.Coordenador;
import com.senac.pi.entities.Curso;
import com.senac.pi.entities.SuperAdmin;
import com.senac.pi.repositories.AlunoRepository;
import com.senac.pi.repositories.CategoriaRepository;
import com.senac.pi.repositories.CoordenadorRepository;
import com.senac.pi.repositories.CursoRepository;
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
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.count() == 0) {

            System.out.println("SISTEMA: Banco vazio. Populando base de dados real para a apresentação...");

            String senhaPadrao = passwordEncoder.encode("123456");

            // ==========================================
            // SUPER ADMIN
            // ==========================================
            SuperAdmin admin = new SuperAdmin(
                null,
                "Administrador Geral",
                "admin@senac.com",
                senhaPadrao
            );
            superAdminRepository.save(admin);


            // ==========================================
            // COORDENADORES
            // ==========================================
            Coordenador leticia = new Coordenador(null, "Letícia", "coordenador@senac.com", senhaPadrao);
            Coordenador ameliara = new Coordenador(null, "Ameliara Freire Santos de Miranda", "ameliaramiranda@pe.senac.br", senhaPadrao);
            Coordenador robson = new Coordenador(null, "ROBSON LUIS TRINDADE LUSTOSA", "robsonlustosa@pe.senac.br", senhaPadrao);
            Coordenador andressa = new Coordenador(null, "Andressa Mendonça da Costa Brito", "andressabrito@pe.senac.br", senhaPadrao);
            Coordenador daniela = new Coordenador(null, "DANIELA VASCONCELOS DE OLIVEIRA", "fac-design@pe.senac.br", senhaPadrao);

            coordenadorRepository.saveAll(Arrays.asList(leticia, ameliara, robson, andressa, daniela));


            // ==========================================
            // CURSOS
            // ==========================================
            Curso ads = new Curso(null, "Análise e Desenvolvimento de Sistemas", "Curso superior focado em desenvolvimento de software", 200);
            Curso jogos = new Curso(null, "Jogos Digitais", "Curso focado em design e programação de jogos", 240);
            Curso gastronomia = new Curso(null, "Gastronomia", "Curso focado em artes culinárias e gestão de cozinhas", 180);
            Curso estetica = new Curso(null, "Estética e Cosmética", "Curso focado em tratamentos estéticos e saúde capilar e facial", 200);
            Curso moda = new Curso(null, "Design de Moda", "Curso focado em estilismo, modelagem e negócios de moda", 200);

            // Mapeando as coordenações nos cursos
            ads.getCoordenadores().addAll(Arrays.asList(leticia, ameliara));
            jogos.getCoordenadores().add(ameliara);
            gastronomia.getCoordenadores().add(robson);
            estetica.getCoordenadores().add(andressa);
            moda.getCoordenadores().add(daniela);

            cursoRepository.saveAll(Arrays.asList(ads, jogos, gastronomia, estetica, moda));

            // Atualizando o lado inverso da relação (Cursos nos Coordenadores)
            leticia.getCursos().add(ads);
            ameliara.getCursos().addAll(Arrays.asList(ads, jogos));
            robson.getCursos().add(gastronomia);
            andressa.getCursos().add(estetica);
            daniela.getCursos().add(moda);
            
            coordenadorRepository.saveAll(Arrays.asList(leticia, ameliara, robson, andressa, daniela));


            // ==========================================
            // CATEGORIAS POR CURSO
            // ==========================================
            // Categorias ADS
            Categoria catAds1 = new Categoria(); catAds1.setArea("Cursos Extensão (ADS)"); catAds1.setHorasPorCertificado(15); catAds1.setLimiteSubmissoesSemestre(5); catAds1.setCurso(ads);
            Categoria catAds2 = new Categoria(); catAds2.setArea("Hackathons e Eventos (ADS)"); catAds2.setHorasPorCertificado(20); catAds2.setLimiteSubmissoesSemestre(3); catAds2.setCurso(ads);

            // Categorias Jogos Digitais
            Categoria catJogos1 = new Categoria(); catJogos1.setArea("Game Jams (Jogos)"); catJogos1.setHorasPorCertificado(25); catJogos1.setLimiteSubmissoesSemestre(2); catJogos1.setCurso(jogos);

            // Categorias Gastronomia
            Categoria catGastro1 = new Categoria(); catGastro1.setArea("Workshops Culinários (Gastro)"); catGastro1.setHorasPorCertificado(10); catGastro1.setLimiteSubmissoesSemestre(4); catGastro1.setCurso(gastronomia);

            // Categorias Estética
            Categoria catEstetica1 = new Categoria(); catEstetica1.setArea("Simpósios de Cosmetologia (Estética)"); catEstetica1.setHorasPorCertificado(12); catEstetica1.setLimiteSubmissoesSemestre(4); catEstetica1.setCurso(estetica);

            // Categorias Moda
            Categoria catModa1 = new Categoria(); catModa1.setArea("Desfiles e Oficinas (Moda)"); catModa1.setHorasPorCertificado(15); catModa1.setLimiteSubmissoesSemestre(4); catModa1.setCurso(moda);

            categoriaRepository.saveAll(Arrays.asList(catAds1, catAds2, catJogos1, catGastro1, catEstetica1, catModa1));


            // ==========================================
            // ALUNOS
            // ==========================================
            // Perfil Principal (Vínculo duplo para testar a nova funcionalidade)
            Aluno caio = new Aluno(
                null, 
                "Caio Victor de Moura Paschoal", 
                "aluno@senac.com", 
                senhaPadrao, 
                "202501001", 
                "ADS/Jogos", 
                0
            );
            caio.addCurso(ads);
            caio.addCurso(jogos);

            // Aluno de Gastronomia
            Aluno alunoGastro = new Aluno(
                null, 
                "Carlos Eduardo Silva", 
                "carlos.gastro@senac.com", 
                senhaPadrao, 
                "202501002", 
                "Gastro-1A", 
                0
            );
            alunoGastro.addCurso(gastronomia);

            // Aluno de Estética
            Aluno alunoEstetica = new Aluno(
                null, 
                "Mariana Costa Souza", 
                "mariana.estetica@senac.com", 
                senhaPadrao, 
                "202501003", 
                "Estética-3N", 
                0
            );
            alunoEstetica.addCurso(estetica);

            // Aluno de Moda
            Aluno alunoModa = new Aluno(
                null, 
                "Beatriz Rocha Lima", 
                "beatriz.moda@senac.com", 
                senhaPadrao, 
                "202501004", 
                "Moda-2V", 
                0
            );
            alunoModa.addCurso(moda);

            alunoRepository.saveAll(Arrays.asList(caio, alunoGastro, alunoEstetica, alunoModa));


            // ==========================================
            // LOG DE CREDENCIAIS PARA A APRESENTAÇÃO
            // ==========================================
            System.out.println("\n==================================================================");
            System.out.println("  BASE DE DADOS ALIMENTADA COM SUCESSO - PRONTA PARA A BANCA  ");
            System.out.println("==================================================================");
            System.out.println("  [ADMINISTRADOR]");
            System.out.println("  - Login: admin@senac.com | Senha: 123456");
            System.out.println("------------------------------------------------------------------");
            System.out.println("  [COORDENADORES]");
            System.out.println("  - Letícia: coordenador@senac.com | Senha: 123456 (ADS)");
            System.out.println("  - Ameliara: ameliaramiranda@pe.senac.br | Senha: 123456 (ADS e Jogos)");
            System.out.println("  - Robson: robsonlustosa@pe.senac.br | Senha: 123456 (Gastronomia)");
            System.out.println("  - Andressa: andressabrito@pe.senac.br | Senha: 123456 (Estética)");
            System.out.println("  - Daniela: fac-design@pe.senac.br | Senha: 123456 (Moda)");
            System.out.println("------------------------------------------------------------------");
            System.out.println("  [ALUNOS (Nenhum certificado pré-carregado)]");
            System.out.println("  - Caio Paschoal: aluno@senac.com | Senha: 123456 (ADS e Jogos Digitais)");
            System.out.println("  - Carlos Eduardo: carlos.gastro@senac.com | Senha: 123456 (Gastronomia)");
            System.out.println("  - Mariana Costa: mariana.estetica@senac.com | Senha: 123456 (Estética)");
            System.out.println("  - Beatriz Rocha: beatriz.moda@senac.com | Senha: 123456 (Moda)");
            System.out.println("==================================================================\n");
        }
    }
}