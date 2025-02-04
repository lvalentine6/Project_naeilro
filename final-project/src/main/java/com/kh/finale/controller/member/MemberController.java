package com.kh.finale.controller.member;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.kh.finale.entity.block.MemberBlockDto;
import com.kh.finale.entity.member.FollowDto;
import com.kh.finale.entity.member.MemberAuthDto;
import com.kh.finale.entity.member.MemberDto;
import com.kh.finale.entity.member.MemberProfileDto;
import com.kh.finale.entity.photostory.PhotostoryListDto;
import com.kh.finale.entity.photostory.PhotostoryPhotoDto;
import com.kh.finale.repository.block.MemberBlockDao;
import com.kh.finale.repository.member.FollowDao;
import com.kh.finale.repository.member.MemberDao;
import com.kh.finale.repository.member.MemberProfileDao;
import com.kh.finale.repository.photostory.PhotostoryDao;
import com.kh.finale.repository.photostory.PhotostoryListDao;
import com.kh.finale.repository.photostory.PhotostoryPhotoDao;
import com.kh.finale.repository.plan.PlannerDao;
import com.kh.finale.service.block.MemberBlockService;
import com.kh.finale.service.member.MemberAuthService;
import com.kh.finale.service.member.MemberEditService;
import com.kh.finale.service.member.MemberFindService;
import com.kh.finale.service.member.MemberJoinService;
import com.kh.finale.vo.member.FollowVo;
import com.kh.finale.vo.member.MemberVo;
import com.kh.finale.vo.photostory.PhotostoryListVO;

@Controller
@RequestMapping("/member")
public class MemberController {

	@Autowired
	MemberDao memberDao; 
	
	@Autowired
	private PhotostoryPhotoDao photostoryPhotoDao; 
	
	@Autowired
	private PhotostoryDao photostoryDao;
	
	@Autowired
	private PhotostoryListDao photostoryListDao;
	
	@Autowired
	private MemberBlockDao memberBlockDao;
	
	@Autowired
	private MemberBlockService memberBlockService;

	// 회원 가입 페이지
	@GetMapping("/join")
	public String join() {
		return "member/join"; 
	}

	@Autowired
	private MemberJoinService memberJoinService;

	@PostMapping("/join")
	public String join(@ModelAttribute MemberVo memberVo) throws IllegalStateException, IOException {
		memberJoinService.memberjoin(memberVo);
		return "redirect:join_success";
	}

	@GetMapping("/join_success")
	public String registSuccess() {
		return "member/joinSuccess";
	}

	// 회원 가입 아이디 중복체크
	@PostMapping("/idCheck")
	@ResponseBody
	public boolean idCheck(@ModelAttribute MemberVo memberVo) {
		boolean idResult = memberFindService.idCheck(memberVo) > 0;
		return idResult;
	}
	
	// 회원가입 닉네임 중복체크
	@PostMapping("/jNickCheck")
	@ResponseBody
	public boolean jNickCheck(@ModelAttribute MemberVo memberVo) {
		boolean Nickresult = memberFindService.jNickCheck(memberVo) > 0;
		return Nickresult;
	}

	// 프로필 편집 닉네임 중복체크
	@PostMapping("/profile/pNickCheck")
	@ResponseBody
	public boolean pNickCheck(@ModelAttribute MemberVo memberVo, HttpSession httpSession) {
		MemberVo Nickresult = memberFindService.pNickCheck(memberVo);
		MemberDto memberDto = memberDao.findInfo((int) httpSession.getAttribute("memberNo"));
		boolean result = false;
		if (ObjectUtils.isEmpty(Nickresult)) {
			result = false;
		} else {
			if (Nickresult.getMemberNick().equals(memberDto.getMemberNick())) {
				result = false;
			} else {
				result = true;
			}
		}
		return result;
	}

	// 로그인 페이지
	@GetMapping("/login")
	public String login() {
		return "member/login";
	}

	// 로그인 처리
	@PostMapping("/login")
	public String login(@ModelAttribute MemberDto memberDto, HttpSession httpSession,Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		MemberDto check = memberDao.login(memberDto);
		
		// 정지 상태일 경우 처리
		if (check!=null && check.getMemberState().equals("정지")) {
			// 정지 해제 체크
			boolean blockCheck = memberBlockDao.checkBlock(check.getMemberNo());
			// 정지 기간이 지났을 경우
			if (blockCheck) {
				memberBlockService.unblock(check.getMemberNo());
			}
			// 정지 기간이 지나지 않았을 경우
			else {
				// 어느 페이지로 보낼지, 보낸 후 어떤 알림창을 띄울 것인지 미정 
				MemberBlockDto memberBlockDto = memberBlockDao.getBlockInfo(check.getMemberNo());
				
				// 정지회원 블럭페이지로 이동
				model.addAttribute("block", memberBlockDto);
				model.addAttribute("msg", "관리자에 의해 계정이 정지 되었습니다.");
				model.addAttribute("reason", memberBlockDto.getBlockReason());
				model.addAttribute("blockEndDate", memberBlockDto.getBlockEndDate());
				model.addAttribute("url", request.getContextPath()); 
				return "member/block";
			}
			return "redirect:/";
		}
		

		if (check != null) {
			httpSession.setAttribute("memberNo", check.getMemberNo());
			httpSession.setAttribute("memberId", check.getMemberId());
			httpSession.setAttribute("memberContextNick", check.getMemberNick());
			return "redirect:/";
		} else {
			return "redirect:login?error";
		}
	}
	
	// 정지회원 로그인 블럭 페이지
	@GetMapping("block")
	public String block() {
		return "redirect:block";
	}
	
	// 로그아웃 처리
	@GetMapping("/logout")
	public String logout(HttpSession httpSession) {
		httpSession.removeAttribute("memberNo");
		httpSession.removeAttribute("memberId");
		httpSession.removeAttribute("memberContextNick");
		return "redirect:/";
	}

	// 아이디 찾기 페이지
	@GetMapping("/findId")
	public String findId() {
		return "member/findId";
	}

	@Autowired
	MemberFindService memberFindService;

	// 아이디 찾기 처리
	@PostMapping("/findId")
	public ModelAndView findId(@ModelAttribute MemberDto memberDto) {
		ModelAndView mav = new ModelAndView();
		Object modelList = memberFindService.findId(memberDto);
		if (modelList == null) {
			mav.setViewName("member/findId");
			mav.addObject("memberDto", memberDto);
			return mav;
		} else {
			mav.setViewName("member/findId");
			mav.addObject("memberDto", modelList);
			return mav;
		}
	}

	// 비밀번호 찾기 페이지
	@GetMapping("/findPw")
	public String findPw(HttpSession session, Model model) {
		// 회원 정보 전송
		int memberNo = 0;
		if (session.getAttribute("memberNo") != null) {
			memberNo = (int) session.getAttribute("memberNo");
			MemberDto memberDto = memberDao.findInfo(memberNo);
			
			model.addAttribute("memberDto", memberDto);
		}
		return "member/findPw";
	}

	@Autowired
	MemberAuthService memberAuthService;

	@Autowired
	MemberAuthDto memberAuthDto;

	// 비밀번호 찾기 (인증번호 발송)
	@PostMapping("sendAuthEmail")
	@ResponseBody
	public Map<String, Object> findPw(@ModelAttribute MemberVo memberVo)
			throws MessagingException, UnsupportedEncodingException {
		Map<String, Object> sendAuthEmail = new HashMap<>();
		MemberVo searchResult = memberFindService.findPw(memberVo);

		MemberAuthDto authResult = memberAuthService.pwSendEmail(searchResult);
		memberAuthService.authInsert(authResult);
		Map<String, Object> memberAuthDto = memberAuthService.resultAuth(authResult);
		return memberAuthDto;

	}

	// 비밀번호 찾기 (반환값 전송)
	@PostMapping("checkAuthEmail")
	@ResponseBody
	public ModelAndView checkAuthEmail(@ModelAttribute MemberAuthDto memberAuthDto) {
		ModelAndView mav = new ModelAndView("jsonView");
		MemberAuthDto checkData = memberFindService.checkAuthEmail(memberAuthDto);
		if (checkData == null) {
			mav.setView(new MappingJackson2JsonView());
			mav.addObject("memberId", memberAuthDto.getMemberId());
			mav.setViewName("null");
			return mav;
		} else {
			mav.setViewName("member/changePw");
			mav.addObject("checkData", checkData);
			return mav;
		}
	}

	// 비밀번호 찾기 (변경 페이지 이동)
	@GetMapping("/changePw")
	public String changePw(@ModelAttribute MemberAuthDto memberAuthDto, Model model, HttpSession session) {
		// 회원 정보 전송
		int memberNo = 0;
		if (session.getAttribute("memberNo") != null) {
			memberNo = (int) session.getAttribute("memberNo");
			MemberDto memberDto = memberDao.findInfo(memberNo);
			
			model.addAttribute("memberDto", memberDto);
		}
		
		MemberAuthDto selectMember = memberAuthService.selectId(memberAuthDto);
		model.addAttribute("memberId", selectMember.getMemberId());
		return "member/changePw";
	}

	// 비밀번호 찾기 (변경 후 메인페이지 리다이렉트)
	@PostMapping("/edit")
	public String edit(@ModelAttribute MemberDto memberDto) {
		memberAuthService.updatePw(memberDto);
		return "redirect:/";
	}

	// 마이페이지 조회 TODO
	@Autowired
	private FollowDao followDao;
	@Autowired
	private PlannerDao plannerDao;
	
	@RequestMapping("/profile/{memberNick}")
	public String myPage(@PathVariable String memberNick
			,Model model,HttpSession session,@ModelAttribute PhotostoryListVO photostoryListVO
			,@RequestParam(required = false) String planner) {
		MemberDto target = memberDao.findWithNick(memberNick);
		model.addAttribute("countPhotostory",photostoryDao.getPhotostoryCountWithMemberNo(target.getMemberNo()));
		photostoryListVO.setMemberNo(target.getMemberNo());
		photostoryListVO.setPageSize(30);
		photostoryListVO = photostoryDao.getPageVariable(photostoryListVO);

		// 마이페이지 회원 정보 전송
		model.addAttribute("profileMemberDto", target);
		
		boolean isFollow = false;
		
		if((Integer)session.getAttribute("memberNo")!=null) {
			// 회원 정보 전송
			MemberDto sessionUser = memberDao.findInfo((int) session.getAttribute("memberNo"));
			model.addAttribute("memberDto", sessionUser);
			
			FollowDto followDto = FollowDto.builder()
					.followFrom((Integer)session.getAttribute("memberNo"))
					.followTo(target.getMemberNo())
					.build();
			if(followDao.isFollow(followDto)!=null) {
				isFollow=true;
			}
		}
		List<MemberDto> tempFollowerList=followDao.getFollowerList(target);
		List<FollowVo> followerList = new ArrayList<>();
		
		for(MemberDto m : tempFollowerList) {
			FollowVo fv = FollowVo.builder()
					.member(m)
					.isFollow(false)
					.build();
			followerList.add(fv);
		}
		
		
		if((Integer)session.getAttribute("memberNo")!=null) {
			for(FollowVo f : followerList) {
				FollowDto followDto = FollowDto.builder()
						.followFrom((Integer)session.getAttribute("memberNo"))
						.followTo(f.getMember().getMemberNo())
						.build();
				if(followDao.isFollow(followDto)!=null) {
					f.setFollow(true);
				}
			}
		}
		List<MemberDto> tempFollowingList=followDao.getFollowingList(target);
		List<FollowVo> followingList = new ArrayList<>();
		
		for(MemberDto m : tempFollowingList) {
			FollowVo fv = FollowVo.builder()
					.member(m)
					.isFollow(false)
					.build();
			followingList.add(fv);
		}
		
		
		if((Integer)session.getAttribute("memberNo")!=null) {
			for(FollowVo f : followingList) {
				FollowDto followDto = FollowDto.builder()
						.followFrom((Integer)session.getAttribute("memberNo"))
						.followTo(f.getMember().getMemberNo())
						.build();
				if(followDao.isFollow(followDto)!=null) {
					f.setFollow(true);
				}
			}
		}
		
		model.addAttribute("followerList",followerList);
		model.addAttribute("followingList",followingList);
		

		model.addAttribute("isFollow",isFollow);
		
		model.addAttribute("countFollower",followDao.getCountFollower(target.getMemberNo()));
		model.addAttribute("countFollowing",followDao.getCountFollowing(target.getMemberNo()));
		
		if(planner!=null&&planner.equals("t")) {
			model.addAttribute("planList", plannerDao.getMemberPlanList(target.getMemberNo()));
			return "member/myPage_plan";
		}
		
		List<PhotostoryListDto> photostoryList = photostoryListDao.listWhitMemberNo(photostoryListVO);
		for (int i = 0; i < photostoryList.size(); i++) {
			PhotostoryListDto photostoryListDto = photostoryList.get(i);
			// 이미지 처리
			List<PhotostoryPhotoDto> photostoryPhotoList = photostoryPhotoDao.get(photostoryListDto.getPhotostoryNo());
			if (!photostoryPhotoList.isEmpty()) {
				photostoryListDto.setPhotostoryPhotoNo(photostoryPhotoList.get(0).getPhotostoryPhotoNo());
			}
		}
		model.addAttribute("photostoryList",photostoryList);
		return "member/myPage";
	}

	@Autowired
	MemberProfileDao memberProfileDao;

	@Autowired
	HttpSession httpSession;

	// 마이페이지 이미지 출력
	@GetMapping("/profile/profileImage")
	public ResponseEntity<ByteArrayResource> image(int memberNo) throws IOException {
		MemberProfileDto memberProfileDto = memberProfileDao.find(memberNo);
		if (memberProfileDto == null) {
			return ResponseEntity.notFound().build();
		}

		File target = new File("D:/upload/kh7e/member", memberProfileDto.getProfileSaveName());
		byte[] data = FileUtils.readFileToByteArray(target);
		ByteArrayResource resource = new ByteArrayResource(data);

		return ResponseEntity.ok().contentLength(memberProfileDto.getProfileSize())
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
				.header(HttpHeaders.CONTENT_ENCODING, "UTF-8")
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
						+ URLEncoder.encode(memberProfileDto.getProfileOriginName(), "UTF-8") + "\"")
				.body(resource);
	}

	// 프로필 편집 페이지
	@GetMapping("/profile/editProfile")
	public String editProfile(Model model) {
		MemberDto memberDto = memberDao.findInfo((int) httpSession.getAttribute("memberNo"));
		memberDto.setMemberNo((int) httpSession.getAttribute("memberNo"));
		model.addAttribute("memberDto", memberDto);
		return "member/editProfile"; 
	}
	
	@Autowired
	MemberEditService memberEditService;

	// 프로필 편집 처리
	@PostMapping("/profile/editProfile")
	public String editProfile(@ModelAttribute MemberVo memberVo, HttpSession httpSession) throws IllegalStateException, IOException {
		memberVo.setMemberNo((int) httpSession.getAttribute("memberNo"));
		memberVo.setMemberId((String) httpSession.getAttribute("memberId"));
		memberEditService.editProfile(memberVo);

		return "redirect:/member/profile/" + URLEncoder.encode(memberVo.getMemberNick(), "UTF-8");
	}

	// 회원 탈퇴
	@GetMapping("/exit")
	public String exit(HttpSession httpSession, MemberVo memberVo) {
		memberVo.setMemberId((String) httpSession.getAttribute("memberId"));
		memberEditService.exitProfile(memberVo);
		memberEditService.exit(memberVo);
		httpSession.removeAttribute("memberNo");
		httpSession.removeAttribute("memberId");
		return "member/exit";
	}

}
