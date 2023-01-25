package cda.flolb.firebasetest;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 Interface pour accéder à l'API RestCountries.
 Utilise Retrofit pour définir les méthodes de requête HTTP.
 **/

public interface RestCountriesApi {

    /**
    Récupère les informations sur tous les pays disponibles auprès de l'API.
    @return une liste d'objets Country contenant les informations sur chaque pays.
    **/

    @GET("all")
    Call<List<Country>> getCountryInfo();
}



