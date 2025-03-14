package Admin;
import java.util.List;

public interface VolleyCallback {
    void onSuccess(List<String> subjects);
    void onError(String errorMessage);
}
