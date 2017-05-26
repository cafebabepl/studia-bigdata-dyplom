package org.kozlowscy.studia.bigdata.dyplom.dictionary;

import com.google.common.base.Joiner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import org.kozlowscy.studia.bigdata.dyplom.model.Country;
import org.kozlowscy.studia.bigdata.dyplom.dictionary.impl.SynonymComThesaurus;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Konstruuje słownik synonimów dla nazw państw w oparciu o listę państw zgodnie ze standardem ISO-3166.
 */
public class CountryDictionaryGenerator {

    private final static Logger log = Logger.getLogger(CountryDictionaryGenerator.class);

    // w nazwach państw występują "," stąd zmiana "domyślnego" separatora
    private final static char SEPARATOR = ';';

    /**
     * Zwraca pierwszy niepusty element.
     */
    private static String coalesce(String... x) {
        for (String i : x) {
            if (StringUtils.isNotBlank(i)) {
                return i;
            }
        }
        return null;
    }

    private static String format(String commonName) {
        // np. "Korea, North" -> "North Korea"
        Matcher matcher = Pattern.compile("(.*),(.*)").matcher(commonName);
        if (matcher.matches()) {
            return String.format("%s %s", matcher.group(2).trim(), matcher.group(1).trim());
        }
        return commonName;
    }

    /**
     * Filtr definicji państw.
     */
    public static Predicate<String> countryFilter() {
        return definition -> definition != null && StringUtils.containsAny(definition.toLowerCase(), "country", "republic", "monarchy", "colony", "kingdom", "parliamentary state", "communist state");
    }

    public static void main(String[] args) throws Exception {
        log.info("Rozpoczęcie tworzenia słownika synonimów dla państw.");
        Thesaurus thesaurus = new SynonymComThesaurus()
                .setAmbiguousFilter(countryFilter());
        Joiner joiner = Joiner.on(SEPARATOR);

        List<String> dictionary =
                // odczyt linii pliku
                Files.lines(Paths.get("data/iso_3166_2_countries.csv"))
                // pominięcie nagłówka pliku
                .skip(1)
                // sparsowanie wiersza dla każdego państwa
                .map(Country::parse)
                // filtr państw niezależnych
                .filter(country -> "Independent State".equalsIgnoreCase(country.Type()))
                .map(country -> {
                    //String word = coalesce(country.formalName(), commonName);
                    String word = format(country.CommonName());
                    // zbiór synonimów
                    Set<String> synonyms = thesaurus.get(word);
                    // złączenie nazwy państwa i zbioru synonimów
                    return joiner.join(word, joiner.join(synonyms));
                })
                .peek(log::debug)
                .collect(Collectors.toList());

        // zapis wynikowego słownika
        File file = new File("data/countries-dictionary.csv");
        log.infof("Zapis słownika do pliku %s.", file.getAbsolutePath());
        FileUtils.writeLines(file, dictionary);
        log.info("Zakończono tworzenie słownika synonimów dla państw.");
    }
}