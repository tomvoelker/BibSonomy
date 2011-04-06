package org.bibsonomy.opensocial.config;

import java.util.List;
import java.util.Set;

import org.apache.shindig.auth.AnonymousAuthenticationHandler;
import org.apache.shindig.auth.AuthenticationHandler;
import org.apache.shindig.common.servlet.ParameterFetcher;
import org.apache.shindig.gadgets.http.BasicHttpFetcher;
import org.apache.shindig.gadgets.http.HttpFetcher;
import org.apache.shindig.protocol.DataServiceServletFetcher;
import org.apache.shindig.protocol.conversion.BeanConverter;
import org.apache.shindig.protocol.conversion.BeanJsonConverter;
import org.apache.shindig.protocol.conversion.BeanXStreamConverter;
import org.apache.shindig.protocol.conversion.xstream.XStreamConfiguration;
import org.apache.shindig.social.core.oauth.AuthenticationHandlerProvider;
import org.apache.shindig.social.core.util.BeanXStreamAtomConverter;
import org.apache.shindig.social.core.util.xstream.XStream081Configuration;
import org.apache.shindig.social.opensocial.service.ActivityHandler;
import org.apache.shindig.social.opensocial.service.AlbumHandler;
import org.apache.shindig.social.opensocial.service.AppDataHandler;
import org.apache.shindig.social.opensocial.service.MediaItemHandler;
import org.apache.shindig.social.opensocial.service.MessageHandler;
import org.apache.shindig.social.opensocial.service.PersonHandler;
import org.bibsonomy.opensocial.security.BibSonomyAuthenticationHandlerProvider;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

/**
 * Provides social api component injection. Implementor may want to replace this module if they need
 * to replace some of the internals of the Social API, like for instance the JSON to Bean to JSON
 * converter Beans, however in general this should not be required, as most default implementations
 * have been specified with the Guice @ImplementedBy annotation.
 */
public class SocialApiGuiceModule extends AbstractModule {

  private static final int CONNECTION_TIMEOUT = 60000;

/** {@inheritDoc} */
  @Override
  protected void configure() {
    bind(ParameterFetcher.class).annotatedWith(Names.named("DataServiceServlet"))
        .to(DataServiceServletFetcher.class);

    bind(Boolean.class)
        .annotatedWith(Names.named(AnonymousAuthenticationHandler.ALLOW_UNAUTHENTICATED))
        .toInstance(Boolean.TRUE);
    bind(XStreamConfiguration.class).to(XStream081Configuration.class);
    bind(BeanConverter.class).annotatedWith(Names.named("shindig.bean.converter.xml")).to(
        BeanXStreamConverter.class);
    bind(BeanConverter.class).annotatedWith(Names.named("shindig.bean.converter.json")).to(
        BeanJsonConverter.class);
    bind(BeanConverter.class).annotatedWith(Names.named("shindig.bean.converter.atom")).to(
        BeanXStreamAtomConverter.class);

    /*
    bind(new TypeLiteral<List<AuthenticationHandler>>(){}).toProvider(
        BibSonomyAuthenticationHandlerProvider.class);
    */
    bind(new TypeLiteral<List<AuthenticationHandler>>(){}).toProvider(AuthenticationHandlerProvider.class);

    Multibinder<Object> handlerBinder = Multibinder.newSetBinder(binder(), Object.class, Names.named("org.apache.shindig.handlers"));
    for (Class handler : getHandlers()) {
      handlerBinder.addBinding().toInstance(handler);
    }
    
    //Configure the basic http fetcher to have a longer request timeout than the default of 5 seconds.
    bind(HttpFetcher.class).toInstance(new BasicHttpFetcher(0, CONNECTION_TIMEOUT, CONNECTION_TIMEOUT, null));
  }

  /**
   * Hook to provide a Set of request handlers.  Subclasses may override
   * to add or replace additional handlers.
   */
  protected Set<Class<?>> getHandlers() {
    return ImmutableSet.<Class<?>>of(ActivityHandler.class, AppDataHandler.class,
        PersonHandler.class, MessageHandler.class, AlbumHandler.class, MediaItemHandler.class);
  }
}