package org.agoncal.application.conference.talk.rest;

import io.swagger.annotations.Api;
import org.agoncal.application.conference.talk.domain.Talk;
import org.agoncal.application.conference.talk.repository.TalkRepository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

/**
 * @author Antonio Goncalves
 *         http://www.antoniogoncalves.org
 *         --
 */
@Path("/talks")
@Api(description = "Talks REST Endpoint")
@RequestScoped
@Produces("application/json")
@Consumes("application/json")
public class TalkEndpoint {

    // ======================================
    // =          Injection Points          =
    // ======================================

    @Inject
    private TalkRepository talkRepository;

    @Context
    private UriInfo uriInfo;

    // ======================================
    // =          Business methods          =
    // ======================================

    @POST
    public Response add(Talk talk) {
        Talk created = talkRepository.create(talk);
        return Response.created(getURIForSelf(talk)).entity(created).build();
    }

    @GET
    @Path("/{id}")
    public Response retrieve(@PathParam("id") String id) {

        Talk talk = talkRepository.findById(id);

        if (talk != null) {
            talk.addLink("self", getURIForSelf(talk));
            return Response.ok(talk).build();
        } else
            return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    public Response allTalks() {
        List<Talk> allTalks = talkRepository.findAllTalks();
        for (Talk talk : allTalks) {
            talk.addLink("self", getURIForSelf(talk));
        }
        return Response.ok(buildEntity(allTalks)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response remove(@PathParam("id") String id) {
        talkRepository.delete(id);
        return Response.noContent().build();
    }

    // ======================================
    // =           Private methods          =
    // ======================================

    private GenericEntity<List<Talk>> buildEntity(final List<Talk> talkList) {
        return new GenericEntity<List<Talk>>(talkList) {
        };
    }

    private URI getURIForSelf(Talk talk) {
        return uriInfo.getBaseUriBuilder().path(TalkEndpoint.class).path(talk.getId()).build();
    }
}
