package mx.fes.unam.zacatlan.util;

import mx.fes.unam.zacatlan.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return username -> usuarioRepository.findByUsername(username)
                .map(u -> org.springframework.security.core.userdetails.User.builder()
                        .username(u.getUsername())
                        .password(u.getPassword())
                        .roles(u.getRol().replace("ROLE_", ""))
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/bootstrap/**", "/css/**", "/js/**", "/img/**").permitAll()

                        // ========== SOLO ADMIN ==========
                        .requestMatchers("/vendedor/**", "/bloque/**", "/producto/**").hasRole("ADMIN")
                        .requestMatchers("/venta/ver_lista", "/venta/editar/**", "/venta/actualizar", "/venta/pdf").hasRole("ADMIN")

                        // ========== VENDEDOR (y admin también puede) ==========
                        .requestMatchers("/venta/mis-ventas", "/venta/mis-ventas/pdf",
                                "/venta/registrar", "/venta/salvar").hasAnyRole("VENDEDOR", "ADMIN")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );
        return http.build();
    }
}