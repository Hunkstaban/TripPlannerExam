package dat.routes;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    TripRoutes tripRoutes = new TripRoutes();

    // Import new routes here
    public EndpointGroup getRoutes() {
        return () -> {
//            path("/<routeName>", <routeName>.getRoutes());
            path("/trips", tripRoutes.getRoutes());

        };
    }
}
