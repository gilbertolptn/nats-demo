package dev.gibatech.nats.demo.model;

import net.datafaker.Faker;

public class PessoaFactory {
    private static final Faker faker = new Faker();

    public static Pessoa criarPessoa(long id) {
        String nome = faker.name().fullName();
        String email = faker.internet().emailAddress();
        return new Pessoa(id, nome, email);
    }
}