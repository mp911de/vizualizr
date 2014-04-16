package biz.paluch.visualizr;

import java.io.StringWriter;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import biz.paluch.visualizr.demo.DemoDatasourceProvider;
import biz.paluch.visualizr.spi.DatasourceProvider;

/**
 * @author <a href="mailto:mark.paluch@1und1.de">Mark Paluch</a>
 * @since 13.04.14 11:26
 */
@Path("view")
public class ViewResource {

    private VelocityEngine velocityEngine;
    private DatasourceProvider datasourceProvider = new DemoDatasourceProvider();

    @PostConstruct
    public void postConstruct() throws Exception {
        Properties p = new Properties();
        p.load(getClass().getResourceAsStream("/velocity.properties"));
        velocityEngine = new VelocityEngine(p);
        velocityEngine.init();
    }

    @GET
    @Path("single.html")
    @Produces(MediaType.TEXT_HTML)
    public String getView(@QueryParam("from") String from, @QueryParam("to") String to,
            @QueryParam("tz") @DefaultValue("UTC") String tz, @QueryParam("datasource") String datasource) throws Exception {

        if (velocityEngine == null) {
            postConstruct();
        }

        Template template = velocityEngine.getTemplate("templates/view.html.vm");
        Context ctx = new VelocityContext();

        ctx.put("renderDatePicker", true);
        ctx.put("renderDataSourcePicker", true);
        ctx.put("localFrameworkResources", true);
        ctx.put("datasources", getDatasourceProvider().getDatasources());

        StringWriter writer = new StringWriter();
        template.merge(ctx, writer);

        return writer.toString();
    }

    public DatasourceProvider getDatasourceProvider() {
        return datasourceProvider;
    }
}
