package com.senac.pi.entities.enums;

public enum StatusSubmissao {
    PENDENTE(1),
    APROVADO(2),
    REJEITADO(3);

    private int code;

    private StatusSubmissao(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static StatusSubmissao valueOf(int code) {
        for (StatusSubmissao value : StatusSubmissao.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Código de Status inválido");
    }
}