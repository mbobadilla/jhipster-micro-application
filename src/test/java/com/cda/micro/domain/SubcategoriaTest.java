package com.cda.micro.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.cda.micro.web.rest.TestUtil;

public class SubcategoriaTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Subcategoria.class);
        Subcategoria subcategoria1 = new Subcategoria();
        subcategoria1.setId(1L);
        Subcategoria subcategoria2 = new Subcategoria();
        subcategoria2.setId(subcategoria1.getId());
        assertThat(subcategoria1).isEqualTo(subcategoria2);
        subcategoria2.setId(2L);
        assertThat(subcategoria1).isNotEqualTo(subcategoria2);
        subcategoria1.setId(null);
        assertThat(subcategoria1).isNotEqualTo(subcategoria2);
    }
}
