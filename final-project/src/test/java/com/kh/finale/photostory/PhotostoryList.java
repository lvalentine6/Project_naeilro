package com.kh.finale.photostory;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.kh.finale.entity.PhotostoryDto;
import com.kh.finale.repository.PhotostoryDao;
import com.kh.finale.vo.PhotostoryVO;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"file:src/main/webapp/WEB-INF/spring/root-context.xml",
	"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml"
})
@WebAppConfiguration
@Slf4j
/**
 * 포토스토리 리스트 테스트
 * @author swjk78
 */
public class PhotostoryList {

	@Autowired
	PhotostoryDao photostoryDao;
	
	@Test
	public void test() {
		PhotostoryVO photostoryVO = PhotostoryVO.builder()
				.startRow(1)
				.endRow(10)
				.pageNo(1)
				.pageSize(10)
				.searchType(null)
				.searchKeyword(null)
				.startBlock(1)
				.endBlock(1)
				.lastBlock(1)
				.build();
		List<PhotostoryDto> list = photostoryDao.list(photostoryVO);
		for (PhotostoryDto photostoryDto : list) {
			log.debug("photostoryDto = {}", photostoryDto);
		}
	}
}
