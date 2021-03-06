package com.airbnb.epoxy.sample.models;

import android.support.v7.widget.RecyclerView.RecycledViewPool;

import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyModelGroup;
import com.airbnb.epoxy.R;
import com.airbnb.epoxy.sample.CarouselData;
import com.airbnb.epoxy.sample.ColorData;
import com.airbnb.epoxy.sample.SampleController.AdapterCallbacks;

import java.util.ArrayList;
import java.util.List;

public class CarouselModelGroup extends EpoxyModelGroup {
  public final CarouselData data;

  public CarouselModelGroup(CarouselData carousel, AdapterCallbacks callbacks,
      RecycledViewPool recycledViewPool) {
    super(R.layout.model_carousel_group, buildModels(carousel, callbacks, recycledViewPool));
    this.data = carousel;
    id(carousel.getId());
  }

  private static List<EpoxyModel<?>> buildModels(CarouselData carousel, AdapterCallbacks callbacks,
      RecycledViewPool recycledViewPool) {
    List<ColorData> colors = carousel.getColors();
    ArrayList<EpoxyModel<?>> models = new ArrayList<>();

    models.add(new ImageButtonModel_()
        .imageRes(R.drawable.ic_add_circle)
        .clickListener(v -> callbacks.onAddColorToCarouselClicked(carousel)));

    models.add(new ImageButtonModel_()
        .imageRes(R.drawable.ic_delete)
        .clickListener(v -> callbacks.onClearCarouselClicked(carousel))
        .show(colors.size() > 0));

    models.add(new ImageButtonModel_()
        .imageRes(R.drawable.ic_change)
        .clickListener(v -> callbacks.onChangeCarouselColorsClicked(carousel))
        .show(colors.size() > 0));

    models.add(new ImageButtonModel_()
        .imageRes(R.drawable.ic_shuffle)
        .clickListener(v -> callbacks.onShuffleCarouselColorsClicked(carousel))
        .show(colors.size() > 1));

    List<ColorModel_> colorModels = new ArrayList<>();
    for (ColorData colorData : colors) {
      colorModels.add(new ColorModel_()
          .id(colorData.getId(), carousel.getId())
          .color(colorData.getColorInt())
          .playAnimation(colorData.shouldPlayAnimation())
          .clickListener((model, parentView, clickedView, position) -> {
            // A model click listener is used instead of a normal click listener so that we can get
            // the current position of the view. Since the view may have been moved when the colors
            // were shuffled we can't rely on the position of the model when it was added here to
            // be correct, since the model won't have been rebound when shuffled.
            callbacks.onColorClicked(carousel, position);
          }));
    }

    models.add(new CarouselModel_()
        .recycledViewPool(recycledViewPool)
        .models(colorModels));

    return models;
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
