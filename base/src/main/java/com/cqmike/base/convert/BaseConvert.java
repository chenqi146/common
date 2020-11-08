package com.cqmike.base.convert;

import com.cqmike.base.entity.BaseEntity;
import com.cqmike.base.form.BaseForm;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mappings;

import java.util.List;

public interface BaseConvert<E extends BaseEntity, F extends BaseForm> {

    @Mappings({})
    @InheritConfiguration
    F toForm(E var1);


    @InheritConfiguration
    List<F> toForms(List<E> var1);


    @InheritInverseConfiguration
    E from(F var1);


    @InheritInverseConfiguration
    List<E> froms(List<F> var1);
}
