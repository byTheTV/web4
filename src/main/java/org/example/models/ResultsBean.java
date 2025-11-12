package org.example.models;

import java.util.ArrayList;
import java.util.List;

public class ResultsBean {
    private final List<Result> results = new ArrayList<>();

    public void add(Result result) {
        results.add(result);
    }

    public List<Result> getResults() {
        return results;
    }
}


