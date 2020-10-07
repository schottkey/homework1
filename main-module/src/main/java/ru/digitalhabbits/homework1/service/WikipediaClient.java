package ru.digitalhabbits.homework1.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class WikipediaClient {
    public static final String WIKIPEDIA_SEARCH_URL = "https://en.wikipedia.org/w/api.php";

    @Nonnull
    public String search(@Nonnull String searchString) {
        final URI uri = prepareSearchUrl(searchString);
        String json = "";

        try (CloseableHttpClient client = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();
             CloseableHttpResponse response = client.execute(new HttpGet(uri))) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                json = EntityUtils.toString(entity);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final JsonObject pages = JsonParser.parseString(json)
                .getAsJsonObject()
                .get("query")
                .getAsJsonObject()
                .get("pages")
                .getAsJsonObject();

        final String pageid = pages.keySet().stream()
                .findFirst()
                .orElse("-1");

        if ("-1".equals(pageid)) {
            throw new RuntimeException("No results found!");
        }

        return pages.get(pageid)
                .getAsJsonObject()
                .get("extract")
                .getAsString();
    }

    @Nonnull
    private URI prepareSearchUrl(@Nonnull String searchString) {
        try {
            return new URIBuilder(WIKIPEDIA_SEARCH_URL)
                    .addParameter("action", "query")
                    .addParameter("format", "json")
                    .addParameter("titles", searchString)
                    .addParameter("prop", "extracts")
                    .addParameter("explaintext", "")
                    .build();
        } catch (URISyntaxException exception) {
            throw new RuntimeException(exception);
        }
    }
}
