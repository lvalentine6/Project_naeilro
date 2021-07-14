package com.kh.finale.controller.photostory;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kh.finale.entity.photostory.PhotostoryCommentListDto;
import com.kh.finale.entity.photostory.PhotostoryDto;
import com.kh.finale.entity.photostory.PhotostoryListDto;
import com.kh.finale.entity.photostory.PhotostoryTotalListDto;
import com.kh.finale.repository.photostory.PhotostoryCommentListDao;
import com.kh.finale.repository.photostory.PhotostoryDao;
import com.kh.finale.repository.photostory.PhotostoryListDao;
import com.kh.finale.repository.photostory.PhotostoryTotalListDao;
import com.kh.finale.util.ListParameter;

@Controller
@RequestMapping("/photostory")
public class PhotostoryViewController {

	@Autowired
	PhotostoryDao photostoryDao;
	
	@Autowired
	PhotostoryListDao photostoryListDao;

	@Autowired
	PhotostoryCommentListDao photostoryCommentListDao;

	@Autowired
	PhotostoryTotalListDao photostoryTotalListDao;
	
	// 포토스토리 리스트 페이지
	@GetMapping("")
	public String home(@ModelAttribute ListParameter listParameter, Model model) {
		listParameter = photostoryDao.getPageVariable(listParameter);
		List<PhotostoryTotalListDto> photostoryTotalList = photostoryTotalListDao.list(listParameter);

		model.addAttribute("listParameter", listParameter);
		model.addAttribute("photostoryTotalList", photostoryTotalList);
		
		return "photostory/photostory";
	}
	
	// 포토스토리 상세 페이지
	@GetMapping("/detail")
	public String detail(@RequestParam int photostoryNo, Model model) {
		PhotostoryListDto photostoryListDto = photostoryListDao.find(photostoryNo);
		List<PhotostoryCommentListDto> photostoryCommentList = photostoryCommentListDao.list(photostoryNo);
		
		model.addAttribute("photostoryListDto", photostoryListDto);
		model.addAttribute("photostoryCommentList", photostoryCommentList);
		
		return "photostory/detail";
	}
	
	// 포토스토리 작성 페이지
	@GetMapping("/write")
	public String write() {
		return "photostory/write";
	}
	
	// 포토스토리 작성 처리
	@PostMapping("/write")
	public String write(@ModelAttribute PhotostoryDto photostoryDto, HttpSession session) {
//		int memberNo = (int) session.getAttribute("memberNo");
		int memberNo = 1;
		photostoryDto.setMemberNo(memberNo);
		photostoryDto.setPlannerNo(1); // 임시
		
		photostoryDao.writePhotostory(photostoryDto);
		
		return "redirect:/photostory";
	}
}
