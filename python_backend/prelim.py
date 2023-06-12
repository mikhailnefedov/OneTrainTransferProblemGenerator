import numpy as np
from scipy.stats import iqr
from sklearn.preprocessing import PowerTransformer


def bound_outliers(array: np.ndarray):
    inter_quartile_range = iqr(array, axis=0)
    quartile_number = 5

    column_min = np.median(array, axis=0) - quartile_number * inter_quartile_range
    column_max = np.median(array, axis=0) + quartile_number * inter_quartile_range
    clipped_array = np.clip(array, column_min, column_max)

    feature_min = np.amin(clipped_array, axis=0)

    for (x, y), value in np.ndenumerate(clipped_array):
        clipped_array[x][y] = clipped_array[x][y] + 1 - feature_min[y]

    return clipped_array


def normalize(array: np.ndarray) -> np.ndarray:
    if array.ndim == 1:
        array = array.reshape(-1, 1)

    pt = PowerTransformer(method="box-cox", standardize=True)
    return pt.fit_transform(array)
