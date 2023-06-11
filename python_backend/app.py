import json

from flask import Flask, request
import numpy as np
from scipy.stats import iqr
from sklearn.preprocessing import PowerTransformer

app = Flask(__name__)


def get_feature_vectors(request):
    feature_vectors = []
    for instance in request.json:
        feature_vectors.append(instance['right'])
    return np.array(feature_vectors)


def bound_outliers(array: np.ndarray):
    inter_quartile_range = iqr(array, axis=0)
    quartile_number = 5

    column_min = np.median(array, axis=0) - 5 * inter_quartile_range
    column_max = np.median(array, axis=0) + 5 * inter_quartile_range
    return np.clip(array, column_min, column_max)


def normalize(array: np.ndarray) -> np.ndarray:
    if array.ndim == 1:
        array = array.reshape(-1, 1)

    pt = PowerTransformer(standardize=True)
    return pt.fit_transform(array)


@app.route('/preprocessing', methods=['POST'])
def preprocessing():
    unprocessed_feature_vectors = get_feature_vectors(request)
    bounded_feature_vectors = bound_outliers(unprocessed_feature_vectors)
    normalized_feature_vectors = normalize(bounded_feature_vectors)
    return {"featureVectors": normalized_feature_vectors.tolist()}


if __name__ == '__main__':
    app.run()
