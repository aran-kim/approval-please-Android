package com.umc.approval.ui.fragment.mypage

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umc.approval.R
import com.umc.approval.data.dto.approval.get.ApprovalPaperDto
import com.umc.approval.data.dto.community.get.CommunityReport
import com.umc.approval.data.dto.community.get.CommunityReportDto
import com.umc.approval.data.dto.community.get.CommunityTok
import com.umc.approval.data.dto.community.get.CommunityTokDto
import com.umc.approval.databinding.FragmentMypageCommentBinding
import com.umc.approval.ui.activity.CommunityReportActivity
import com.umc.approval.ui.activity.CommunityTokActivity
import com.umc.approval.ui.activity.DocumentActivity
import com.umc.approval.ui.adapter.approval_fragment.ApprovalPaperListRVAdapter
import com.umc.approval.ui.adapter.community_fragment.CommunityReportItemRVAdapter
import com.umc.approval.ui.adapter.community_fragment.CommunityTalkItemRVAdapter
import com.umc.approval.ui.adapter.home_fragment.PopularPostRVAdapter
import com.umc.approval.ui.fragment.approval.ApprovalBottomSheetDialogStatusFragment
import com.umc.approval.ui.viewmodel.mypage.MyPageCommentViewModel

/*
  MyPage 댓글 tab View
 */
class MypageCommentFragment : Fragment() {

    private var _binding : FragmentMypageCommentBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<MyPageCommentViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentMypageCommentBinding.inflate(inflater, container, false)
        val view = binding.root

        var state : Int? = null
        var type : Int? = null

        live_data()

        viewModel.get_my_comments(type, state)

        binding.cgFilter.setOnCheckedStateChangeListener { chipGroup, checkedIds ->
            Log.d("로그", "서류 종류 선택, $checkedIds")
            // checkedIds에 따라 API 호출, 리사이클러뷰 갱신
            when(chipGroup.checkedChipId){
                binding.chipApproval.id -> {
                    type = null
                    viewModel.get_my_comments(type, state)
                }
                binding.chipTok.id -> {
                    type = 0
                    viewModel.get_my_comments(type, state)
                }
                binding.chipReport.id -> {
                    type = 1
                    viewModel.get_my_comments(type, state)
                }
            }
        }

        binding.stateSelect.setOnClickListener {
            val bottomSheetDialog = ApprovalBottomSheetDialogStatusFragment(binding.stateText.text.toString())
            bottomSheetDialog.setStyle(
                DialogFragment.STYLE_NORMAL,
                R.style.RoundCornerBottomSheetDialogTheme
            )
            bottomSheetDialog.show(childFragmentManager, bottomSheetDialog.tag)
        }

        childFragmentManager
            .setFragmentResultListener("status", this) { _, bundle ->
                val result = bundle.getString("result")
                binding.stateText.text = result

                // 리사이클러뷰 아이템 갱신
                Log.d("status", result.toString())
                when(result){
                    "상태 전체" -> {
                        state = null
                        viewModel.get_my_comments(type, state)
                    }
                    "승인됨" -> {
                        state = 0
                        viewModel.get_my_comments(type, state)
                    }
                    "반려됨" -> {
                        state = 1
                        viewModel.get_my_comments(type, state)
                    }
                    "결재 대기중" -> {
                        state = 2
                        viewModel.get_my_comments(type, state)
                    }
                }
            }
        return view
    }

    override fun onStart() {
        super.onStart()
    }

    /**
     * viewBinding이 더이상 필요 없을 경우 null 처리 필요
     */
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    /* 결재 서류 불러 오는 함수 */
    private fun live_data(){
        viewModel.comment.observe(viewLifecycleOwner){
            if (it.documentContent != null) {
                val paperRVAdapter =
                    ApprovalPaperListRVAdapter(ApprovalPaperDto(it.documentCount?:0, it.documentContent?: listOf()))
                binding.rvMypageComment.adapter = paperRVAdapter
                binding.rvMypageComment.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

                // 클릭 이벤트 처리
                paperRVAdapter.setOnItemClickListener(object :
                    ApprovalPaperListRVAdapter.OnItemClickListner {
                    override fun onItemClick(v: View, data: ApprovalPaper, pos: Int) {
                        //결재 서류 아이디를 통해 상세보기로 이동
                        val intent = Intent(requireContext(), DocumentActivity::class.java)
                        intent.putExtra("documentId", data.documentId.toString())
                        startActivity(intent)
                    }
                })

            } else if (it.toktokContent != null) {
                binding.rvMypageComment.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                val talkRVAdapter = CommunityTalkItemRVAdapter(CommunityTokDto(it.toktokCount?:0,it.toktokContent?: listOf()))
                binding.rvMypageComment.adapter = talkRVAdapter

                // 클릭 이벤트 처리
                talkRVAdapter.itemClick = object : CommunityTalkItemRVAdapter.ItemClick {
                    override fun move_to_tok_activity(v: View, data: CommunityTok, pos: Int) {

                        //toktok Id 전달
                        val intent = Intent(requireContext(), CommunityTokActivity::class.java)
                        intent.putExtra("toktokId", data.toktokId.toString())
                        startActivity(intent)
                    }
                }
            } else if (it.reportContent != null) {
                binding.rvMypageComment.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                val reportRVAdapter = CommunityReportItemRVAdapter(CommunityReportDto(it.reportCount?:0,it.reportContent?: listOf()))
                binding.rvMypageComment.adapter = reportRVAdapter

                reportRVAdapter.itemClick = object : CommunityReportItemRVAdapter.ItemClick {
                    override fun move_to_report_activity(v: View, data: CommunityReport, pos: Int) {
                        //report Id 전달
                        val intent = Intent(requireContext(), CommunityReportActivity::class.java)
                        intent.putExtra("reportId", data.reportId.toString())
                        startActivity(intent)
                    }
                    override fun move_to_document_activity(v: View, data: CommunityReport, pos: Int) {
                        //report Id 전달
                        val intent = Intent(requireContext(), DocumentActivity::class.java)
                        intent.putExtra("documentId", data.document.documentId.toString())
                        startActivity(intent)
                    }
                }
            }
        }
    }
}