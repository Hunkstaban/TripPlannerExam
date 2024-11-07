package dat.controllers;

import dat.config.HibernateConfig;
import dat.daos.GuideDAO;
import dat.dtos.GuidePriceDTO;
import dat.exceptions.ErrorResponse;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class GuideController {

    private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory(false);
    private static GuideDAO guideDAO = GuideDAO.getInstance(emf);


    public void getGuidePriceOverview(Context ctx) {
        try {
            List<GuidePriceDTO> overview = guideDAO.getGuidePriceOverview();
            ctx.json(overview);
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse(500, "Failed to retrieve guide price overview"));
        }
    }
}
