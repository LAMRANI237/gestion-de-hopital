package edu.ezip.ing1.pds.business.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ezip.ing1.pds.business.dto.Student;
import edu.ezip.ing1.pds.business.dto.Students;
import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.commons.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class XMartCityService {

    private final static String LoggingLabel = "B u s i n e s s - S e r v e r";
    private final Logger logger = LoggerFactory.getLogger(LoggingLabel);

    private enum Queries {
        SELECT_ALL_MEDICAMENTS("SELECT t.nom_medicament, t.description, t.effet_secondaire,t.medicament_id FROM \"public\".gestion t"),
        INSERT_MEDICAMENT("INSERT into \"public\".gestion (\"nom_medicament\", \"description\", \"effet_secondaire\") values (?, ?, ?)");
        private final String query;

        private Queries(final String query) {
            this.query = query;
        }
    }

    public static XMartCityService inst = null;
    public static final XMartCityService getInstance()  {
        if(inst == null) {
            inst = new XMartCityService();
        }
        return inst;
    }

    private XMartCityService() {

    }

    public final Response dispatch(final Request request, final Connection connection)
            throws InvocationTargetException, IllegalAccessException, SQLException, NoSuchFieldException, IOException {
        if(request.getRequestOrder().equals("SELECT_ALL_MEDICAMENTS")){
            PreparedStatement preparedStatement = connection.prepareStatement(Queries.SELECT_ALL_MEDICAMENTS.query);
            ResultSet result = preparedStatement.executeQuery();
            Students listMedicament = new Students();
            while(result.next()){
                Student medicament = new Student().build(result);
                listMedicament.add(medicament);
            }
            ObjectMapper mapper = new ObjectMapper();
           return new Response(request.getRequestId(), mapper.writeValueAsString(listMedicament));
        }else if(request.getRequestOrder().equals("INSERT_MEDICAMENT")){
            PreparedStatement preparedStatement = connection.prepareStatement(Queries.INSERT_MEDICAMENT.query);
            ObjectMapper mapper = new ObjectMapper();
            Student student = mapper.readValue(request.getRequestBody(), Student.class);
            Integer line = student.build(preparedStatement).executeUpdate();
            Response response = new Response();
            response.setResponseBody("{\"medicament_id\": " + line + "}");                
            response.setRequestId(request.getRequestId());
            return response;
        } else {
            return null;
        }


        
    }

}
