import io
import json

from flask import Flask, request
import numpy as np

import prelim
from prelim import bound_outliers, normalize, get_bounds_and_bound_outliers, get_normalization_lambdas

app = Flask(__name__)


def create_feature_vector(instances, feature_names):
    instances_and_feature_vectors = []
    for instance in instances:
        feature_values = []
        for feature_name in feature_names:
            feature_values.append(instance["featureDescription"][feature_name])
        instances_and_feature_vectors.append((instance, np.array(feature_values)))
    return instances_and_feature_vectors


@app.route('/prelimdata', methods=['POST'])
def get_outlier_normalize_data():
    json_data = request.json
    instances_and_feature_vectors = create_feature_vector(json_data["instances"], json_data["featureNames"])
    unprocessed_feature_vectors = np.array([pair[1] for pair in instances_and_feature_vectors])
    column_min, column_max, feature_min, bounded_feature_vectors = get_bounds_and_bound_outliers(
        unprocessed_feature_vectors)
    lambdas = get_normalization_lambdas(bounded_feature_vectors)

    data = []
    for i in range(0, len(json_data["featureNames"])):
        feature_data = {
            "featureName": json_data["featureNames"][i],
            "columnMin": column_min[i],
            "columnMax": column_max[i],
            "featureMin": feature_min[i],
            "lambda": lambdas[i]
        }
        data.append(feature_data)
    return {"featureData": data}


if __name__ == '__main__':
    app.run()
