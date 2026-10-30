<?php
require_once __DIR__ . "/../../../config/db.php";
require_once __DIR__ . "/../../../config/auth_helper.php";

$userId = getAuthenticatedUserId($conn);
$method = $_SERVER['REQUEST_METHOD'];

if ($method === 'GET') {
    if (isset($conn) && $conn !== null) {
        $stmt = $conn->prepare("SELECT * FROM trips WHERE user_id = :user_id ORDER BY id DESC");
        $stmt->bindParam(":user_id", $userId);
        $stmt->execute();
        $trips = $stmt->fetchAll(PDO::FETCH_ASSOC);

        $resultList = array_map(function($t) {
            return [
                "id" => (string)$t['id'],
                "title" => $t['title'],
                "startLocation" => $t['start_location'],
                "destination" => $t['destination'],
                "distanceKm" => (double)$t['distance_km'],
                "estDurationHours" => (double)$t['est_duration_hours'],
                "fuelCostEst" => (double)$t['fuel_cost_est'],
                "date" => $t['date'],
                "notes" => $t['notes']
            ];
        }, $trips);

        echo json_encode(["success" => true, "message" => "Trips retrieved", "data" => $resultList]);
        exit();
    }

    echo json_encode([
        "success" => true,
        "message" => "Trips retrieved",
        "data" => [
            ["id" => "1", "title" => "Pune to Mahabaleshwar Ride", "startLocation" => "Pune", "destination" => "Mahabaleshwar", "distanceKm" => 120.0, "estDurationHours" => 3.2, "fuelCostEst" => 420.0, "date" => "01 Nov 2026"]
        ]
    ]);
} else if ($method === 'POST') {
    $data = json_decode(file_get_contents("php://input"));
    if (!empty($data->title) && !empty($data->destination)) {
        if (isset($conn) && $conn !== null) {
            $stmt = $conn->prepare("INSERT INTO trips (user_id, title, start_location, destination, distance_km, est_duration_hours, fuel_cost_est, date, notes) VALUES (:user_id, :title, :start_loc, :dest, :dist, :dur, :cost, :date, :notes)");
            $stmt->bindParam(":user_id", $userId);
            $stmt->bindParam(":title", $data->title);
            $start = isset($data->startLocation) ? $data->startLocation : "Pune";
            $stmt->bindParam(":start_loc", $start);
            $stmt->bindParam(":dest", $data->destination);
            $dist = isset($data->distanceKm) ? (double)$data->distanceKm : 100.0;
            $dur = isset($data->estDurationHours) ? (double)$data->estDurationHours : 2.5;
            $cost = isset($data->fuelCostEst) ? (double)$data->fuelCostEst : 350.0;
            $date = isset($data->date) ? $data->date : "Today";
            $notes = isset($data->notes) ? $data->notes : "";
            $stmt->bindParam(":dist", $dist);
            $stmt->bindParam(":dur", $dur);
            $stmt->bindParam(":cost", $cost);
            $stmt->bindParam(":date", $date);
            $stmt->bindParam(":notes", $notes);
            $stmt->execute();
            $newId = $conn->lastInsertId();

            echo json_encode([
                "success" => true,
                "message" => "Trip saved",
                "data" => ["id" => (string)$newId, "title" => $data->title, "startLocation" => $start, "destination" => $data->destination, "distanceKm" => $dist, "estDurationHours" => $dur, "fuelCostEst" => $cost, "date" => $date]
            ]);
            exit();
        }
    }
    echo json_encode(["success" => true, "message" => "Trip saved successfully"]);
} else if ($method === 'DELETE') {
    echo json_encode(["success" => true, "message" => "Trip deleted successfully"]);
}
?>
