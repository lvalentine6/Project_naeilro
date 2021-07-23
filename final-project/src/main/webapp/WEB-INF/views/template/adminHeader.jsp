<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/css/bootstrap.min.css"
    integrity="sha384-B0vP5xmATw1+K9KRQjQERJvTumQW0nPEzvF6L/Z6nronJ3oUOFUFpCjEUQouq2+l" crossorigin="anonymous">
<link rel="stylesheet" href="../css/layout.css"/>
<style>
	
	main{
		padding-top: 120px; 
		padding-bottom: 60px;
	}
	li{
		list-style: none;
	}
	 a {color:#000;text-decoration: none; outline: none}

	 a:hover, a:active {text-decoration: none;}

	nav{
		position: fixed;
		width: 100%;
		z-index: 99;
	}
	nav ul>li{
		margin-left: 2rem;
		
	}
	
	.user_profile{
		border-radius: 100%;
		object-fit:cover;
	}
	.user_profile_sm{
		width: 35px;
		height: 35px;
	}
	.user_profile_lg{
		width: 140px;
		height: 140px;
	}
	.story-photo{
		width: 100%;
		object-fit:cover;
	}
	.text-sm{
		font-size: 14px;
	}
	.like{
		color : #ed4956;
	}
</style>
<!-- icon -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.8.2/css/all.min.css"/>
<!-- bootstrap 이용을 위한 JS 의존성 등록 (jQuery/popper/BS) -->
<script src="https://code.jquery.com/jquery-3.6.0.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"
    integrity="sha384-9/reFTGAW83EW2RDu2S0VKaIzap3H66lZH81PoYlFhbGU+6BZp6G7niu735Sk7lN" crossorigin="anonymous">
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/js/bootstrap.min.js"
    integrity="sha384-+YQ4JLhjyBLPDQt//I+STsc9iw4uQqACwlvpslubQzn4u2UU2UFM80nGisd026JF" crossorigin="anonymous">
</script>
<script>
	window.addEventListener("load",function(){
		const exit = document.querySelector(".confirm-link")
		exit.addEventListener("click",function(e){
			var message = this.dataset.message
			if(!message){
				message = "이동하겠습니까?"
			}
			if (!window.confirm(message) ){ 
				e.preventDefault()
			}
		})
	})
</script>
</head>
<body>
	
	<nav class="bg-white shadow-sm">
		<div class="container-lg">
			<div class="row">
				<div class="col-2">
					<h1 class="h-100 text-nowrap"><a href="${root}">NAEILRO</a></h1>
				</div>
				<div class="col-7">
					<ul class="d-flex align-items-center h-100 font-weight-bold">
						<li><a class="text-nowrap" href="#">회원관리</a></li>
						<li><a class="text-nowrap" href="#">댓글관리</a></li>
						<li><a class="text-nowrap" href="#">스토리관리</a></li>
					</ul>
				</div>
				<div class="col-3">
					<ul class="d-flex justify-content-end align-items-center h-100 font-weight-bold">
						<li><a class="text-nowrap" href="#">로그아웃</a></li>
					</ul>
				</div>
			</div>
		</div>
	</nav>