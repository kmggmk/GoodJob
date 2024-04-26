<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="/WEB-INF/views/inc/asset.jsp" %>
<style>

</style>
</head>
<%@include file="/WEB-INF/views/inc/header.jsp" %>
<body>
<section class="">
  <div class="container max-w-full">
    <div class="row">
      <div class="min-h-[980px] bg-white py-10 lg:col-6 lg:py-[114px]">
        <div class="mx-auto w-full max-w-[480px]">
          <img class="mb-8" src="images/flower.svg" alt="" />
          <h1 class="mb-4">Sing In</h1>
          <p>Donec sollicitudin molestie malesda sollitudin</p>
          <div class="signin-options mt-10">
            <a class="btn btn-outline-white block w-full text-dark" href="#"
              >Sign In With Google</a
            >
          </div>
          <div
            class="relative my-8 text-center after:absolute after:left-0 after:top-1/2 after:z-[0] after:w-full after:border-b after:border-border after:content-['']"
          >
            <span class="relative z-[1] inline-block bg-white px-2"
              >Or Sign In With Email</span
            >
          </div>

          <form action="#">
            <div class="form-group">
              <label for="email" class="form-label">Email Adrdess</label>
              <input
                type="email"
                id="email"
                class="form-control"
                placeholder="Your Email Address"
              />
            </div>
            <div class="form-group mt-4">
              <label for="password" class="form-label">Password</label>
              <input
                type="password"
                id="password"
                class="form-control"
                placeholder="Your Password"
              />
            </div>
            <input
              class="btn btn-primary mt-10 block w-full"
              type="submit"
              value="Sign In"
            />
            <p class="mt-6 text-center">
              Can't <span class="text-dark" href="#">log in</span>?
              <a class="text-dark" href="signup.html">Sign up</a> for create
              account
            </p>
          </form>
        </div>
      </div>

      <div
        class="auth-banner bg-gradient flex flex-col items-center justify-center py-16 lg:col-6"
      >
        <img
          class="absolute top-0 left-0 h-full w-full"
          src="images/login-banner-bg.svg"
          alt=""
        />
        <div class="w-full text-center">
          <h2 class="h3 text-white">
            Turn your All ideas into<br />
            your reality
          </h2>
          <div class="swiper auth-banner-carousel">
            <div class="swiper-wrapper">
              <div class="swiper-slide">
                <img
                  width="667"
                  height="557"
                  class="mx-auto"
                  src="images/login-carousel-img-1.png"
                  alt=""
                />
              </div>
              <div class="swiper-slide">
                <img
                  width="667"
                  height="557"
                  class="mx-auto"
                  src="images/login-carousel-img-1.png"
                  alt=""
                />
              </div>
              <div class="swiper-slide">
                <img
                  width="667"
                  height="557"
                  class="mx-auto"
                  src="images/login-carousel-img-1.png"
                  alt=""
                />
              </div>
            </div>
            <div class="pagination"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</section>	
	
<%@include file="/WEB-INF/views/inc/footer.jsp" %>	
	<script>

	</script>
</body>
</html>