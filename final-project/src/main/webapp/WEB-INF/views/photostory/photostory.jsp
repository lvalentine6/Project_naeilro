<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>
<script>
	$(function() {
		/* 좋아요 버튼 */
		$(".like-btn").each(function() {
			$(this).click(function() {
				if(${memberNo==null }){
					alert("로그인후 이용해주세요");
				}
					/* 좋아요 삭제 */
				if ($(this).hasClass("like")) {
					$.ajax({
						url:"${pageContext.request.contextPath}/process/delete_like",
						data : {
							photostoryNo : $(this).attr("data-photostoryNo"),
						},
						method:"GET",
						dataType : "json"
					})
					.done(function(){
						$(this).removeClass("like")
						$(this).removeClass("fas")
						$(this).addClass("far")
						let curval = $(this).parent().parent().next().children().children().text() * 1;
						$(this).parent().parent().next().children().children().text(curval-1)
					})
					.fail(function(){
						alert("입력한 정보와 일치하는 회원정보가 없습니다.")
					})
					
				} else {
					/* 좋아요 추가 */
					$.ajax({
						url:"${pageContext.request.contextPath}/process/insert_like",
						data : {
							photostoryNo : $(this).attr("data-photostoryNo"),
						},
						method:"GET",
						dataType : "json"
					})
					.done(function(){
						$(this).removeClass("far")
						$(this).addClass("like")
						$(this).addClass("fas")
						
						let curval = $(this).parent().parent().next().children().children().text() * 1;
						$(this).parent().parent().next().children().children().text(curval+1)
					})
					.fail(function(){
					})
					
				}
			})
		})
		
		$(".coment-btn").each(function(){
			$(this).click(function(){
				let comment = $(this).parent().prev().children().val();
				let curEl = $(this);
				if(!comment){
					return;
				}
				
				console.log(comment);
				$.ajax({
					url:"${pageContext.request.contextPath}/process/insert_comment",
					data : {
						photostoryNo : $(this).attr("data-photostoryNo"),
						photostoryCommentContent : comment
					},
					method:"POST",
				})
				.done(function(){
					let template = $("#comment-tpl").html();
					template = template.replace("{{userId}}",${memberNo})
					template = template.replace("{{comment}}",comment)
					$(curEl).parent().parent().prev().prev().append(template)
					console.log(curEl);
				})
				.fail(function(){
					console.log('fail');
				})
			})
		})
	})
</script>
<script type="text/template" id="comment-tpl">
	<div class="col-12 text-sm">
		<strong>{{userId}}</strong>&nbsp;&nbsp;{{comment}}
	</div>
</script>
<main>
	<c:forEach var="photostoryTotalListDto" items="${photostoryTotalList}">
		<div class="container-lg ">
			<div class="row justify-content-center ">
				<div class=" col-lg-8 offset-lg-2 mx-2">
					<div class='border row align-items-center'>
						<div class="col-1">
							<img class="my-2 user_profile_sm user_profile"
								src="${pageContext.request.contextPath}/image/default_user_profile.jpg">
						</div>
						<div class="col-3 font-weight-bold text-nowrap">${photostoryTotalListDto.memberNick}글작성자
							닉네임</div>
						<div class="col-1 offset-7 text-right">
							<i class="fas fa-ellipsis-h"></i>
						</div>
					</div>
					<div class=' row align-items-center'>
						<img class="w-100 border"
							src="${pageContext.request.contextPath}/image/bgimg.webp" />
					</div>
					<div class='row align-items-center border-left border-right'>
						<div class="col-1 py-2">
							<%-- <c:choose>
								<c:when test="${photostoryTotalListDto.isLike}">
									<i class="fa-heart fa-lg like-btn fas like" data-photostoryNo="${photostoryTotalListDto.photostoryNo}"></i>
								</c:when>
								<c:otherwise>
									<i class="fa-heart fa-lg like-btn far" data-photostoryNo="${photostoryTotalListDto.photostoryNo}"></i> 
								</c:otherwise>
							</c:choose> --%>
							<i class="fa-heart fa-lg like-btn far"
								data-photostoryNo="${photostoryTotalListDto.photostoryNo}"></i>
						</div>
						<div class="col-1">
							<i class="far fa-comment fa-lg"></i>
						</div>
						<div class="col-10"></div>
					</div>
					<div class='row align-items-center border-left border-right'>
						<div class="col-12 text-sm">
							좋아요 <span> ${photostoryTotalListDto.photostoryLikeCount}</span>
						</div>
					</div>
					<div class='row align-items-center border-left border-right mb-1'>
						<div class="col-12 text-sm">
							<strong>${photostoryTotalListDto.memberNick}글작성자 닉네임</strong>&nbsp;&nbsp;${photostoryTotalListDto.photostoryContent}
							아무글 아무글아무글아무글 아무글아무글 아무글아무글아무글아무글 아무글아무글아무글아무글
						</div>
					</div>
					<div class='row align-items-center border-left border-right mb-1'>
						<div class="col-12 text-black-50 font-weight-bold text-sm">댓글
							${photostoryTotalListDto.photostoryCommentCount}개 모두 보기</div>
					</div>
					<div class='row align-items-center border-left border-right mb-1'>
						<div class="col-12 text-sm">
							<strong>user_test_id2</strong>&nbsp;&nbsp;아무댓글아무댓글아무댓글아무댓글아무댓글아무댓글아무댓글아무댓글아무댓글아무댓글아무댓글아무댓글
						</div>
						<div class="col-12 text-sm">
							<strong>user_test_id3</strong>&nbsp;&nbsp;아무댓글아무댓글아무댓글아무댓글아무댓글아무댓글아무댓글아무댓글아무댓글아무댓글아무댓글아무댓글
						</div>
						<div class="col-12 text-sm">
							<strong>user_test_id4</strong>&nbsp;&nbsp;아무댓글아무댓글아무댓글아무댓글아무댓글아무댓글아무댓글아무댓글아무댓글아무댓글아무댓글아무댓글
						</div>
					</div>
					<div
						class='row align-items-center border-left border-right border-bottom pb-3'>
						<div
							class="col-12 text-black-50 font-weight-bold text-right text-sm ">${photostoryTotalListDto.getPastDateString()}</div>
					</div>
					<div
						class='row align-items-center border-left border-right border-bottom mb-3 py-2'>
						<div class="col-10">
							<input type="text" class="form-control border-0"
								placeholder="댓글 달기 . . .">
						</div>
						<div class="col-2 text-right">
							<button type="button"
								class="btn btn-outline-primary text-nowrap coment-btn"
								data-photostoryNo="${photostoryTotalListDto.photostoryNo}">게시</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</c:forEach>
</main>
<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>


