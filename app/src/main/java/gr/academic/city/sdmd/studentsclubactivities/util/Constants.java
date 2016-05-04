package gr.academic.city.sdmd.studentsclubactivities.util;

/**
 * Created by trumpets on 4/13/16.
 */
public final class Constants {

    private Constants() {}

    public static final String SERVER_URL = "http://clubs-sdmdcity.rhcloud.com/rest/";
    public static final String CLUBS_URL = SERVER_URL + "clubs";
    public static final String CLUB_ACTIVITIES_URL = SERVER_URL + "clubs/{0}/activities";
    public static final String DELETE_CLUB_ACTIVITIES_URL = SERVER_URL + "clubs/{0}/activities/{1}";

    public static final String CLUB_ACTIVITIES_DATE_FORMAT = "dd/MM/yyyy";
}
