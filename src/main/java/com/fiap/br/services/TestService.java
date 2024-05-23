package com.fiap.br.services;

import java.util.List;

import com.fiap.br.models.Test;
import com.fiap.br.repositories.TestRepository;

public class TestService {

    private TestRepository testRepository;

    public TestService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    public Test findTestById(int id) {
        return testRepository.findOne(Test.class, id);
    }

    public List<Test> findAllTests() {
        return testRepository.findAll(Test.class);
    }

    public void saveTest(Test test) {
        testRepository.save(test);
    }

    public void updateTest(Test test, int id) {
        testRepository.update(test, id);
    }

    public void deleteTest(int id) {
        testRepository.delete(Test.class, id);
    }
}
