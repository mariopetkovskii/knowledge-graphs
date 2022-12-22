package com.example.knowledgegraph;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDFS;
import org.springframework.scheduling.config.Task;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Jena {
    static final String URL = "https://dbpedia.org/data/The_Beatles.ttl";

    static final String inputFileName = System.getProperty("user.dir") + "\\src\\main\\java\\com\\example\\knowledgegraph\\hifm-dataset-bio2rdf.ttl";

    private static void Task1(){
        Model model = ModelFactory.createDefaultModel();
        model.read(URL);
//        model.write(System.out, "TURTLE");
        System.out.println("\n-----\n");
        Property hometown = model.getProperty("http://dbpedia.org/ontology/hometown");
        ResIterator iter = model.listResourcesWithProperty(RDFS.label);
        while(iter.hasNext()){
            Resource resource = iter.nextResource();
//
//            Model modelHometown = model.read(resource.getProperty(hometown).getObject().toString());
//            modelHometown.write(System.out, "TURTLE");
            System.out.println(resource.getProperty(hometown).getObject());
            String [] splitArray = (resource.getProperty(hometown).getObject()).toString().split("/");
            String hometownLabel = splitArray[splitArray.length-1];
            System.out.println(hometownLabel);

            String liverPoolUrl = "https://dbpedia.org/data/" + hometownLabel + ".ttl";
            Model model2 = ModelFactory.createDefaultModel();
            model2.read(liverPoolUrl);

            ResIterator resIteratorLiverPool = model2.listResourcesWithProperty(RDFS.comment);

            while(resIteratorLiverPool.hasNext()){
                Resource resourceLiverpoolComment = resIteratorLiverPool.nextResource();
                System.out.println("Name and surname: " + resourceLiverpoolComment.getProperty(RDFS.comment).getObject());
            }


        }
    }

    public static void Task2(){
        Model model = ModelFactory.createDefaultModel();
        InputStream inputStream = FileManager.get().open(inputFileName);
        if(inputStream == null) {
            throw new IllegalArgumentException("File: " + inputFileName + " not found!");
        }
        model.read(inputStream, "", "TURTLE");
//        model.write(System.out, "TURTLE");

        String SPARQLEndpoint = "https://bio2rdf.org/sparql";

        Resource resource = model.getResource("http://purl.org/net/hifm/data#987964");
        Property property1 = model.getProperty("http://www.w3.org/2000/01/rdf-schema#seeAlso");
        StmtIterator iter = model.listStatements(resource, property1, (RDFNode) null);
        while(iter.hasNext()){
            Statement statement = iter.nextStatement();
            RDFNode object = statement.getObject();

            String queryString = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
                    + "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                    + "prefix dcterms: <http://purl.org/dc/terms/>"
                    + "SELECT * "
                    + "WHERE {<"
                    + object
                    + "> rdfs:label ?label ; "
                    + "dcterms:title ?title ;"
                    + "dcterms:description ?desc ."
                    + "}";
            System.out.println();
            System.out.println("SPARQL query = " + queryString);

            Query query = QueryFactory.create(queryString);

            try(QueryExecution execution = QueryExecutionFactory.sparqlService(SPARQLEndpoint, query)){
                ResultSet resultSet = execution.execSelect();
                while (resultSet.hasNext()) {
                    QuerySolution solution = resultSet.nextSolution();
                    System.out.println();
                    System.out.println("Label: " + solution.get("label"));
                    System.out.println("Title: " + solution.get("title"));
                    System.out.println("Description: " + solution.get("desc"));
                }
            }
        }

    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println("Task 1:");
        Task1();
        System.out.println();
        System.out.println("Task 2:");
        Task2();

    }
}
