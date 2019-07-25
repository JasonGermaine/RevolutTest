package com.jasongermaine.revoluttest.resources;

import java.util.List;
import java.util.function.Supplier;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.jasongermaine.revoluttest.core.Transfer;
import com.jasongermaine.revoluttest.core.TransferRequest;
import com.jasongermaine.revoluttest.transfer.TransferExecutor;

@Path("/transfer")
@Produces(MediaType.APPLICATION_JSON)
public class TransferResource {
  private static final Logger LOG = LoggerFactory.getLogger(TransferResource.class);
  private final TransferExecutor transferExecutor;
  private final Supplier<List<Transfer>> transferSupplier;

  @Inject
  public TransferResource(TransferExecutor transferExecutor,
                          Supplier<List<Transfer>> transferSupplier) {
    this.transferExecutor = transferExecutor;
    this.transferSupplier = transferSupplier;
  }

  @POST
  public Response createAccount(TransferRequest transferRequest) {
    try {
      return Response.ok(transferExecutor.apply(transferRequest)).build();
    } catch (IllegalArgumentException e) {
      LOG.warn("Failed to execute transfer for request={}", transferRequest, e);
      return Response.status(Status.BAD_REQUEST).build();
    }
  }

  @GET
  public List<Transfer> getAllTransfers() {
    return transferSupplier.get();
  }
}
