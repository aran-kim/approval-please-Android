package Collie

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.umc.approval.databinding.*

/*
알림 페이지 활동 탭 Fragment
 */
class NotificationActivityFragment : Fragment() {

    private var _binding : FragmentNotificationActivityBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentNotificationActivityBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onStart() {
        super.onStart()
        binding.notificationActivityRecyclerview.layoutManager = LinearLayoutManager(this.context)
        val itemList = ArrayList<NotificationItem>()
        for(i in 0.. 15 ){
            itemList.add(NotificationItem("김부장", "님이 내 결재 서류를 반려했습니다.", "댓글 내용 예시 텍스트 공간", "2023.01.14"))
        }

        val notificationAdapter = NotificationAdapter(itemList)
        notificationAdapter.notifyDataSetChanged()

        binding.notificationActivityRecyclerview.adapter = notificationAdapter
    }

    /**
     * viewBinding이 더이상 필요 없을 경우 null 처리 필요
     */
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}