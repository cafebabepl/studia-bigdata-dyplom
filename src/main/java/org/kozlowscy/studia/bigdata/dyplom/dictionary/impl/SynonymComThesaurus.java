package org.kozlowscy.studia.bigdata.dyplom.dictionary.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.jboss.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.kozlowscy.studia.bigdata.dyplom.dictionary.Thesaurus;

/**
 * Słownik synonimów w oparciu o stronę
 * <a href="http://www.synonym.com/">http://www.synonym.com/</a>
 *
 * @author Włodzimierz Kozłowski
 */
public class SynonymComThesaurus implements Thesaurus {

    private final static Logger log = Logger.getLogger(SynonymComThesaurus.class);
    // bazowy adres słownika
    private final static String QUERY_URL = "http://www.synonym.com/synonym";

    private Predicate<String> ambiguousFilter = x -> true;

    public SynonymComThesaurus setAmbiguousFilter(Predicate<String> filter) {
        if (filter == null) {
            throw new IllegalArgumentException("Brak funkcji filtrującej.");
        }
        this.ambiguousFilter = filter;
        return this;
    }

    @Override
    public Set<String> get(String word) {
        log.infof("Określenie zbioru synonimów dla słowa %s.", word);
        try {
            String url = String.format("%s/%s", QUERY_URL, word);
            log.infof("Pobranie treści z adresu %s", url);
            Document document = Jsoup
                    .connect(url)
                    .userAgent("Mozilla/5.0")
                    .get();

            log.tracef("%s", document.body());
            // pominięcie słów o podobnym brzmieniu
            if (document.body().outerHtml().contains("We couldn't find any exact matches")) {
                log.info("Nie znaleziono żadnych synonimów. Pominięcie słów podobnych.");
                return Collections.emptySet();
            }

            Elements synonyms = document.select("div.synonym");
            log.infof("Liczba znalezionych znaczeń słowa '%s': %d.", word, synonyms.size());

            Set<String> result = new HashSet<>();
            for (Element synonym : synonyms) {
                String term = synonym.select("h3.term").text();
                String definition = synonym.select("p.definition").text();
                log.debugf("%s: %s", term, definition);
                // filtrowanie niejednoznacznych definicji
                if (synonyms.size() == 1 || ambiguousFilter.test(definition)) {
                    log.debug("Przyjęto bieżącą definicję.");
                    Elements syns = synonym.select("li.syn");
                    for (Element syn : syns) {
                        String _word = syn.text().trim();
                        log.debugf("\t%s", _word);
                        result.add(_word);
                    }
                }
            }
            return result;
        } catch (Exception ex) {
            // pusty zbiór w przypadku wystąpienia błędu
            log.error(ex);
            return Collections.emptySet();
        }
    }

}