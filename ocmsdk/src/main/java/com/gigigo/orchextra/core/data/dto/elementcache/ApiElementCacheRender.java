package com.gigigo.orchextra.core.data.dto.elementcache;


import com.gigigo.orchextra.core.data.dto.article.ApiArticleElement;
import java.util.List;

public class ApiElementCacheRender {

  private String contentUrl;
  private String url;

  private String title;
  private List<ApiArticleElement> elements;

  private String uri;

  private String source;
  private String format;

  public String getContentUrl() {
    return contentUrl;
  }

  public String getUrl() {
    return url;
  }

  public String getTitle() {
    return title;
  }

  public List<ApiArticleElement> getElements() {
    return elements;
  }

  public String getUri() {
    return uri;
  }

  public String getSource() {
    return source;
  }

  public String getFormat() {
    return format;
  }
}
