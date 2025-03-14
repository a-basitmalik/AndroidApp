package Shared.Attendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lms.R;
import java.util.List;
import Models.StudentAttendance;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {
    private List<StudentAttendance> attendanceList;
    private Context context;

    public AttendanceAdapter(List<StudentAttendance> attendanceList) {
        this.attendanceList = attendanceList;
    }

    public void updateData(List<StudentAttendance> newData) {
        this.attendanceList = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.attendance_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentAttendance attendance = attendanceList.get(position);
        holder.tvName.setText(attendance.getStudentName());
        holder.tvId.setText(String.valueOf(attendance.getStudentId())); // Fixed conversion issue
        holder.tvStatus.setText(attendance.isPresent() ? "Present" : "Absent");

        int color = ContextCompat.getColor(context,
                attendance.isPresent() ? R.color.success_color : R.color.error_color);
        holder.tvStatus.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvId, tvStatus;

        ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvId = view.findViewById(R.id.tvId);
            tvStatus = view.findViewById(R.id.tvStatus);
        }
    }
}
