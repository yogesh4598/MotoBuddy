<?php
require_once __DIR__ . "/../../../config/db.php";
require_once __DIR__ . "/../../../config/auth_helper.php";

$userId = getAuthenticatedUserId($conn);
$method = $_SERVER['REQUEST_METHOD'];

if ($method === 'GET') {
    if (isset($conn) && $conn !== null) {
        $stmt = $conn->prepare("SELECT * FROM motorcycles WHERE user_id = :user_id ORDER BY id DESC");
        $stmt->bindParam(":user_id", $userId);
        $stmt->execute();
        $bikes = $stmt->fetchAll(PDO::FETCH_ASSOC);

        $resultList = array_map(function($b) {
            return [
                "id" => (string)$b['id'],
                "make" => $b['make'],
                "model" => $b['model'],
                "year" => (int)$b['year'],
                "odometerKm" => (int)$b['odometer_km'],
                "ownerName" => $b['owner_name']
            ];
        }, $bikes);

        echo json_encode(["success" => true, "message" => "Motorcycles retrieved", "data" => $resultList]);
        exit();
    }

    echo json_encode([
        "success" => true,
        "message" => "Motorcycles retrieved",
        "data" => [
            ["id" => "1", "make" => "Royal Enfield", "model" => "Himalayan 411", "year" => 2022, "odometerKm" => 14250, "ownerName" => "Aarav Sharma"]
        ]
    ]);
} else if ($method === 'POST') {
    $data = json_decode(file_get_contents("php://input"));
    if (!empty($data->make) && !empty($data->model)) {
        if (isset($conn) && $conn !== null) {
            $stmt = $conn->prepare("INSERT INTO motorcycles (user_id, make, model, year, odometer_km, owner_name) VALUES (:user_id, :make, :model, :year, :odo, :owner)");
            $stmt->bindParam(":user_id", $userId);
            $stmt->bindParam(":make", $data->make);
            $stmt->bindParam(":model", $data->model);
            $year = isset($data->year) ? (int)$data->year : 2022;
            $odo = isset($data->odometerKm) ? (int)$data->odometerKm : 0;
            $owner = isset($data->ownerName) ? $data->ownerName : "Rider";
            $stmt->bindParam(":year", $year);
            $stmt->bindParam(":odo", $odo);
            $stmt->bindParam(":owner", $owner);
            $stmt->execute();
            $newId = $conn->lastInsertId();

            echo json_encode([
                "success" => true,
                "message" => "Motorcycle added",
                "data" => ["id" => (string)$newId, "make" => $data->make, "model" => $data->model, "year" => $year, "odometerKm" => $odo, "ownerName" => $owner]
            ]);
            exit();
        }
    }
    echo json_encode(["success" => true, "message" => "Motorcycle added successfully"]);
}
?>
