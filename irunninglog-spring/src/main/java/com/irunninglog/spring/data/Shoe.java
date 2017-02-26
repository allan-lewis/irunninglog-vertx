package com.irunninglog.spring.data;

import com.irunninglog.api.data.IShoe;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
final class Shoe extends AbstractData<IShoe> implements IShoe {

    private String startDate;
    private String max;

    public Shoe() {
        super();

        logger.debug("Created an instance {}", hashCode());
    }

    @Override
    public IShoe setStartDate(String startDate) {
        this.startDate = startDate;
        return this;
    }

    @Override
    public IShoe setMax(String max) {
        this.max = max;
        return this;
    }

    @Override
    public String getStartDate() {
        return startDate;
    }

    @Override
    public String getMax() {
        return max;
    }

}
