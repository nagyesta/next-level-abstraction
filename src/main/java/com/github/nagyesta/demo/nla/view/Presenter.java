package com.github.nagyesta.demo.nla.view;

import com.github.nagyesta.demo.nla.model.world.World;

public interface Presenter {

    void renderFrame(Appendable appendable, World world);
}
