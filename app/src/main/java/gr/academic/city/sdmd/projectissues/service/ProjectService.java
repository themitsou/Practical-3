package gr.academic.city.sdmd.projectissues.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import gr.academic.city.sdmd.projectissues.db.ProjectManagementContract;
import gr.academic.city.sdmd.projectissues.domain.Project;
import gr.academic.city.sdmd.projectissues.domain.Projects;
import gr.academic.city.sdmd.projectissues.util.Commons;
import gr.academic.city.sdmd.projectissues.util.Constants;
import gr.academic.city.sdmd.projectissues.util.GsonResponseCallback;

import static gr.academic.city.sdmd.projectissues.util.Commons.executeRequest;

/**
 * Created by trumpets on 4/13/16.
 */
public class ProjectService extends IntentService {

    private static final String ACTION_FETCH_PROJECTS = "gr.academic.city.sdmd.projectissues.FETCH_PROJECTS";

    public static void startFetchProjects(Context context) {
        Intent intent = new Intent(context, ProjectService.class);
        intent.setAction(ACTION_FETCH_PROJECTS);

        context.startService(intent);
    }

    public ProjectService() {
        super("ProjectService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ACTION_FETCH_PROJECTS.equals(intent.getAction())) {
            fetchProjects();
        } else {
            throw new UnsupportedOperationException("Action not supported: " + intent.getAction());
        }
    }

    private void fetchProjects() {
        executeRequest(Constants.PROJECTS_URL, Commons.ConnectionMethod.GET, null, new GsonResponseCallback<Projects>(Projects.class) {

            @Override
            protected void onResponse(int responseCode, Projects projects) {
                for (Project project : projects.getProjects()) {
                    if (getContentResolver().query(
                           ProjectManagementContract.Project.CONTENT_URI,
                            null,
                            ProjectManagementContract.Project.COLUMN_NAME_SERVER_ID + " = ?",
                            new String[]{String.valueOf(project.getServerId())},
                            null).getCount() == 0) {

                        // this project is not in db, add it
                        getContentResolver().insert(
                                ProjectManagementContract.Project.CONTENT_URI,
                                project.toContentValues());
                    }
                }
            }
        });
    }
}
