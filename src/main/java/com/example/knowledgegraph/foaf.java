package com.example.knowledgegraph;

import org.apache.jena.base.Sys;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDFS;

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.List;

public class foaf {
    public static void main(String[] args) {
        Model model = ModelFactory.createDefaultModel();
        model.read("https://mariopetkovskii.github.io/rdf-foaf/foaf.ttl");
//        model.write(System.out, "TTL");

        Property seeAlso = model.getProperty("http://www.w3.org/2000/01/rdf-schema#seeAlso");
        Property foaf = model.getProperty("http://xmlns.com/foaf/0.1/knows");
        Property fullName = model.getProperty("http://xmlns.com/foaf/0.1/name");
        Property email = model.getProperty("http://xmlns.com/foaf/0.1/mbox_sha1sum");
        Property homepage = model.getProperty("http://xmlns.com/foaf/0.1/homepage");
        Property photo = model.getProperty("http://xmlns.com/foaf/0.1/depiction");
        Property workPlaceHomepage = model.getProperty("http://xmlns.com/foaf/0.1/workplaceHomepage");

        Resource resource = model.getResource("https://mariopetkovskii.github.io#me");

        List<String> urls = new ArrayList<>();

        StmtIterator iterator = model.listStatements(resource, foaf, (RDFNode) null);
        while (iterator.hasNext()){
            Statement statement = iterator.nextStatement();

            String url = statement.getObject().asResource().getProperty(seeAlso).getObject().toString();

            urls.add(url);
        }


        urls.forEach(url -> {

            System.out.println();

            System.out.println("----------------");
            Model model2 = ModelFactory.createDefaultModel();
            model2.read(url);
            Resource resource2 = model2.getResource(url + "#me");

            System.out.println("URL: " + url);

            StmtIterator fullNameIterator = model2.listStatements(resource2, fullName, (RDFNode) null);
            StmtIterator emailIterator = model2.listStatements(resource2, email, (RDFNode) null);
            StmtIterator homepageIterator = model2.listStatements(resource2, homepage, (RDFNode) null);
            StmtIterator photoIterator = model2.listStatements(resource2, photo, (RDFNode) null);
            StmtIterator workplaceHomepageIterator = model2.listStatements(resource2, workPlaceHomepage, (RDFNode) null);

            while(fullNameIterator.hasNext()){
                Statement fullNameStatement = fullNameIterator.nextStatement();
                Statement emailStatement = emailIterator.nextStatement();
                Statement homepageStatement = homepageIterator.nextStatement();

                System.out.println(fullNameStatement.getObject());
                System.out.println(emailStatement.getObject());
                System.out.println(homepageStatement.getObject());

                try{
                    Statement photoStatement = photoIterator.nextStatement();
                    System.out.println(photoStatement.getObject());
                } catch (Exception e){
                    System.out.println("There is no photo for " + fullNameStatement.getObject());
                }

                try{
                    Statement workPlaceHomepageStatement = workplaceHomepageIterator.nextStatement();
                    System.out.println(workPlaceHomepageStatement.getObject());
                } catch (Exception e){
                    System.out.println("There is workplace homepage for " + fullNameStatement.getObject());
                }
                System.out.println();
                System.out.println("---------------------");

            }



        });




    }
}
