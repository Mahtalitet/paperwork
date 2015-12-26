package hu.supercluster.paperwork;

import android.content.Context;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Paperwork {
    public static final String DEFAULT_FILENAME = "paperwork.json";
    public static final String GIT_SHA = "gitSha";
    public static final String BUILD_TIME = "buildTime";
    public static final String ENV = "env";
    public static final String EXTRAS = "extras";

    private final Context context;
    private String filename;
    private JSONObject json;

    public Paperwork(Context context) {
        this(context, DEFAULT_FILENAME);
    }

    public Paperwork(Context context, String filename) {
        this.context = context;
        this.filename = filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getGitSha() {
        initAndCheck();

        return json.optString(GIT_SHA);
    }

    public String getBuildTime() {
        initAndCheck();

        return json.optString(BUILD_TIME);
    }

    public String getEnv(String key) {
        initAndCheck();

        return json.optJSONObject(ENV).optString(key);
    }

    public JSONObject getExtras() {
        initAndCheck();

        return json.optJSONObject(EXTRAS);
    }

    public String getExtra(String key) {
        initAndCheck();

        return getExtras().optString(key);
    }

    private void initAndCheck() {
        if (json == null) {
            try {
                json = getJsonContents();

            } catch (JSONException e) {
                throw new PaperworkException("The file '%s' contains invalid JSON data", filename, e);
            }

            check(json);
        }
    }

    private JSONObject getJsonContents() throws JSONException {
        JSONObject jsonObject = new JSONObject(getFileContents());

        return jsonObject;
    }

    @NonNull
    private String getFileContents() {
        StringBuilder builder;

        try {
            InputStream stream = context.getAssets().open(filename);
            BufferedReader in = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            builder = new StringBuilder();
            String str;

            while ((str = in.readLine()) != null) {
                builder.append(str);
            }

            in.close();

        } catch (IOException e) {
            throw new PaperworkException("There was an error parsing the file '%s'", filename, e);
        }

        return builder.toString();
    }

    private void check(JSONObject jsonObject) {
        if (!jsonObject.has(GIT_SHA) ||
            !jsonObject.has(BUILD_TIME) ||
            !jsonObject.has(ENV) ||
            !jsonObject.has(EXTRAS)) {

            throw new PaperworkException("The file '%s' is missing paperwork data", filename);
        }
    }
}
