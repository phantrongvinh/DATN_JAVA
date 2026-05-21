<template>
  <div class="container-fluid">
    <div class="p-2 d-flex justify-content-between">
      <RouterLink to="/" class="fw-bold fs-3 text-decoration-none text-dark">M</RouterLink>
      <div class="d-flex">
        <form class="d-flex" role="search">
          <input class="form-control me-2" type="search" placeholder="Search" aria-label="Search" />
          <button class="btn btn-outline-success" type="submit">Search</button>
        </form>
        <div class="mx-3">Cart</div>
        <RouterLink to="/login" class="btn btn-outline-primary text-decoration-none"
          >Login</RouterLink
        >
      </div>
    </div>
    <nav>
      <ul class="nav justify-content-center">
        <li class="nav-item">
          <RouterLink to="/" class="nav-link text-decoration-none text-dark">Home</RouterLink>
        </li>
        <li class="nav-item"><a href="" class="nav-link"></a></li>
        <li class="nav-item"><a href="" class="nav-link"></a></li>
        <li class="nav-item"><a href="" class="nav-link"></a></li>
        <li class="nav-item"><a href="" class="nav-link"></a></li>
        <li class="nav-item"><a href="" class="nav-link"></a></li>
      </ul>
    </nav>
  </div>
</template>

<script setup></script>
